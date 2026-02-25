package com.atjiao.cloud.rabbitMq.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.UserMessage;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.rabbitMq.config.RabbitMQConfig;
import com.atjiao.cloud.service.MessageService;
import com.atjiao.cloud.service.MessageTypeService;
import com.atjiao.cloud.service.UserInformationService;
import com.atjiao.cloud.service.UserMessageService;
import com.atjiao.cloud.util.FastJsonUtil;
import com.atjiao.cloud.ws.MessageChatEndpoint;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.atjiao.cloud.domain.SysMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author 焦叶鹏
 * * @data 2025/10/14 19:47
 * @description: RabbitMQ消费者
 **/
@Component
@Slf4j
//@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final RabbitTemplate rabbitTemplate;
    @Resource
    private MessageTypeService messageTypeService;
    @Resource
    private MessageService messageService;
    @Resource
    private UserMessageService userMessageService;
    @Resource
    private UserInformationService userInformationService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // ============================================================
    // ① 即时用户消息消费
    // ============================================================

    /**
     * 监听主通知队列（即时消息）
     * 注意：即时消息处理需要快速响应，避免事务阻塞
     */
    @RabbitListener(queues = RabbitMQConfig.USER_NOTIFICATION_QUEUE, containerFactory = "manualAckContainerFactory")
    public void handleUserNotification(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[即时消息] 收到用户通知: {}", body);
            /**
             * 1.通过body的userId参数判断是发送给单个用户还是全体用户
             * 2.建立websocket连接,将消息发送过去
             */

            // 解析消息内容
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            String userId = jsonObject.getString("userId");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");

            // 模拟业务处理
            Thread.sleep(200);

            // 同步日志到审计交换机
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUDIT_EXCHANGE, "", body);
            log.info("即时消息同步到 audit.exchange");

            // 发送WebSocket通知
            if ("all".equals(userId)) {
                // 全体用户通知
                MessageChatEndpoint.broadcastNotification(jsonObject);
            } else {
                // 检查用户是否在线
                Set<String> onlineUsers = MessageChatEndpoint.getOnlineUsers();

                // 获取用户信息
                UserInformation userInformation = userInformationService.getOne(
                    Wrappers.<UserInformation>lambdaQuery().eq(UserInformation::getId, userId));

                if (userInformation != null) {
                    // 根据userId获取用户名，这里假设用户名和userId相同
                    String username = userInformation.getAccountNumber();

                    if (onlineUsers.contains(username)) {
                        // 用户在线，发送消息  添加消息状态
                        jsonObject.put("status","online");
                        boolean sent = MessageChatEndpoint.sendNotificationToUser(username, jsonObject);
                        log.info("即时消息发送结果: {}", sent);
                        // 注意：发送状态的统一处理已移到handleNotificationLog方法中
                    } else {
                        // 用户不在线，发送到离线消息队列
                        log.info("用户[{}]不在线，消息进入离线队列", username);
                        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.RK_OFFLINE_MESSAGE, body);
                        // 注意：发送状态的统一处理已移到handleNotificationLog方法中，即使是离线用户也设置为已发送
                    }
                } else {
                    log.warn("未找到用户信息，userId: {}", userId);
                }
            }

            // 处理成功后确认消息
            channel.basicAck(deliveryTag, false);
            log.info("即时消息处理完成并确认，deliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            log.error("即时消息消费失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false); // 拒绝并不重回队列，触发备份交换机
                log.info("即时消息处理失败并拒绝确认，deliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                log.error("即时消息拒绝确认失败，deliveryTag: {}", deliveryTag, ioException);
            }
        }
    }

    // ============================================================
    // ② 延迟用户消息消费（针对 delay.exchange）
    // ============================================================

    /**
     * 监听延迟消息（单用户延迟通知）
     * 该队列只接收单个用户的延迟消息，路由键格式：notification.delayed.user.{userId}
     */
    @RabbitListener(queues = RabbitMQConfig.DELAYED_USER_NOTIFICATION_QUEUE, containerFactory = "manualAckContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleDelayedUserNotification(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[延迟消息-单用户] 收到延迟通知: {}", body);

            // 解析消息内容
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            String userId = jsonObject.getString("userId");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");

            Thread.sleep(200);

            channel.basicAck(deliveryTag, false);

            rabbitTemplate.convertAndSend(RabbitMQConfig.AUDIT_EXCHANGE, "", body);
            log.info("延迟用户消息同步到 audit.exchange");

            // 获取在线用户列表
            Set<String> onlineUsers = MessageChatEndpoint.getOnlineUsers();

            // 获取用户信息
            UserInformation userInformation = userInformationService.getOne(
                Wrappers.<UserInformation>lambdaQuery().eq(UserInformation::getId, userId));

            if (userInformation != null) {
                String username = userInformation.getAccountNumber();

                if (onlineUsers.contains(username)) {
                    // 用户在线，发送消息
                    boolean sent = MessageChatEndpoint.sendNotificationToUser(username, jsonObject);
                    // 注意：发送状态的统一处理已移到handleNotificationLog方法中
                } else {
                    // 用户不在线，发送到离线消息队列
                    log.info("用户[{}]不在线，消息进入离线队列", username);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.RK_OFFLINE_MESSAGE, body);
                    // 注意：发送状态的统一处理已移到handleNotificationLog方法中，即使是离线用户也设置为已发送
                }
            } else {
                log.warn("未找到用户信息，userId: {}", userId);
            }

        } catch (Exception e) {
            log.error("延迟用户消息处理失败: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 监听延迟消息（全体用户通知）
     * 该队列只接收全体用户的延迟消息，路由键：notification.delayed.user.all
     */
    @RabbitListener(queues = RabbitMQConfig.DELAYED_ALL_NOTIFICATION_QUEUE, containerFactory = "manualAckContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleDelayedAllNotification(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[延迟消息-全体用户] 收到全体延迟通知: {}", body);

            // 解析消息内容
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");

            Thread.sleep(200);

            channel.basicAck(deliveryTag, false);

            rabbitTemplate.convertAndSend(RabbitMQConfig.AUDIT_EXCHANGE, "", body);
            log.info("延迟全体消息同步到 audit.exchange");

            // 全体用户通知 - 需要循环判断每个用户的在线状态
            Set<String> onlineUsers = MessageChatEndpoint.getOnlineUsers();
            List<UserInformation> userList = userInformationService.list();

            for (UserInformation user : userList) {
                String username = user.getAccountNumber();

                if (onlineUsers.contains(username)) {
                    // 用户在线，发送消息
                    boolean sent = MessageChatEndpoint.sendNotificationToUser(username, jsonObject);
                    // 注意：发送状态的统一处理已移到handleNotificationLog方法中
                } else {
                    // 用户不在线，发送到离线消息队列
                    log.info("用户[{}]不在线，消息进入离线队列", username);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.RK_OFFLINE_MESSAGE, body);
                    // 注意：发送状态的统一处理已移到handleNotificationLog方法中，即使是离线用户也设置为已发送
                }
            }

        } catch (Exception e) {
            log.error("延迟全体消息处理失败: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // ============================================================
    // ③ 日志队列消费
    // ============================================================

    /**
     * 监听审计日志队列，模拟写入数据库
     * 注意：该方法使用事务管理，但消息确认需要在事务提交后进行
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_LOG_QUEUE, containerFactory = "manualAckContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleNotificationLog(Message message, Channel channel) throws ParseException, IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[日志队列] 消息持久化: {}", body);

            SysMessage sysMessage = new SysMessage();
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            String userId = jsonObject.getString("userId");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");
            String pushTime = jsonObject.getString("pushTime");
            String messageType = jsonObject.getString("messageType");
            Long realUserId = jsonObject.getLong("realUserId");

            if(userId.equals("all")){
                /**
                 * 全体用户推送
                 * 需要遍历全体用户
                 */
                sysMessage.setSenderId(realUserId);
                sysMessage.setContent(content);
                sysMessage.setTitle(title);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(pushTime);
                sysMessage.setPushTime(date);
                if(messageType == null || messageType.isEmpty()){
                    messageType=("其他消息");
                }
                Long messageTypeId = messageTypeService.getOne(Wrappers.<MessageType>lambdaQuery().eq(MessageType::getTypeName,messageType)).getId();
                sysMessage.setMessageTypeId(messageTypeId);
                sysMessage.setPushScope(2);
                //设置推送状态
                sysMessage.setPushStatus(1);

                //保存后台管理员发送的消息信息
                boolean messageSaved = messageService.save(sysMessage);
                log.info("保存后台管理员发送的消息信息结果: {}, messageId: {}", messageSaved, sysMessage.getId());

                //保存前台用户接收的消息信息 - 注意：统一设置发送状态为已发送(1)，因为消息已经进入队列处理
                List<UserInformation> userList = userInformationService.list();
                for (UserInformation user : userList) {
                    UserMessage userMessage = new UserMessage();
                    userMessage.setMessageId(sysMessage.getId());
                    userMessage.setUserId(user.getId());
                    userMessage.setReadStatus(0);
                    userMessage.setSendStatus(1); // 统一设置为已发送，因为消息已经被推送到队列
                    boolean userMessageSaved = userMessageService.save(userMessage);
                    log.info("保存用户消息结果: {}, userId: {}, messageId: {}, 发送状态: 已发送", userMessageSaved, user.getId(), sysMessage.getId());
                }

            }else {
                sysMessage.setSenderId(realUserId);
                sysMessage.setContent(content);
                sysMessage.setTitle(title);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(pushTime);
                sysMessage.setPushTime(date);
                if(messageType == null || messageType.isEmpty()){
                    messageType=("其他消息");
                }
                Long messageTypeId = messageTypeService.getOne(Wrappers.<MessageType>lambdaQuery().eq(MessageType::getTypeName,messageType)).getId();
                sysMessage.setMessageTypeId(messageTypeId);
                sysMessage.setPushScope(1);
                //设置推送状态
                sysMessage.setPushStatus(1);

                //保存后台管理员发送的消息信息
                boolean messageSaved = messageService.save(sysMessage);
                log.info("保存后台管理员发送的消息信息结果: {}, messageId: {}", messageSaved, sysMessage.getId());

                //保存前台用户接收的消息信息 - 注意：统一设置发送状态为已发送(1)，因为消息已经进入队列处理
                UserMessage userMessage = new UserMessage();
                userMessage.setMessageId(sysMessage.getId());
                userMessage.setUserId(Long.valueOf(userId));
                userMessage.setReadStatus(0);
                userMessage.setSendStatus(1); // 统一设置为已发送，因为消息已经被推送到队列
                boolean userMessageSaved = userMessageService.save(userMessage);
                log.info("保存用户消息结果: {}, userId: {}, messageId: {}, 发送状态: 已发送", userMessageSaved, userId, sysMessage.getId());
            }

            // 在事务提交后手动确认消息
            // 使用@Transactional事务管理，Spring会在方法执行完成后自动提交事务
            // 我们需要确保在事务提交后再确认消息
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        channel.basicAck(deliveryTag, false);
                        log.info("消息确认成功，deliveryTag: {}", deliveryTag);
                    } catch (IOException e) {
                        log.error("消息确认失败，deliveryTag: {}", deliveryTag, e);
                    }
                }
            });

        } catch (Exception e) {
            log.error("日志队列消息处理失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
                log.info("消息拒绝确认，deliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                log.error("消息拒绝确认失败，deliveryTag: {}", deliveryTag, ioException);
            }
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    // ============================================================
    // ④ 备份队列消费
    // ============================================================

    /**
     * 监听备份队列（未被正确路由的消息或消费失败的消息）
     */
    @RabbitListener(queues = RabbitMQConfig.BACKUP_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleBackupMessage(Message message) throws ParseException {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        //这里持久化的是第一次发送失败的消息
        log.warn("[备份队列] 收到未路由或失败的消息: {}", body);
        //先同步记录到日志中,再重新发送到审计队列
        SysMessage sysMessage = new SysMessage();
        JSONObject jsonObject = FastJsonUtil.parseSafe(body);
        String userId = jsonObject.getString("userId");
        String content = jsonObject.getString("content");
        String title = jsonObject.getString("title");
        String pushTime = jsonObject.getString("pushTime");
        String messageType = jsonObject.getString("messageType");
        Long realUserId = jsonObject.getLong("realUserId");
        if(userId.equals("all")){
            /**
             * 全体用户推送
             * 需要遍历全体用户
             */
            sysMessage.setSenderId(realUserId);
            sysMessage.setContent(content);
            sysMessage.setTitle(title);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(pushTime);
            sysMessage.setPushTime(date);
            if(messageType == null || messageType.isEmpty()){
                messageType=("其他消息");
                // throw new RuntimeException("消息类型为空");
            }
            Long messageTypeId = messageTypeService.getOne(Wrappers.<MessageType>lambdaQuery().eq(MessageType::getTypeName,messageType)).getId();
            sysMessage.setMessageTypeId(messageTypeId);
            sysMessage.setPushScope(2);
            //设置推送状态
            sysMessage.setPushStatus(2);
            //过期时间是否需要设计

            //保存后台管理员发送的消息信息
            messageService.save(sysMessage);

            //保存前台用户接收的消息信息
            List<UserInformation> userList = userInformationService.list();
            for (UserInformation user : userList) {
                UserMessage userMessage = new UserMessage();
                userMessage.setMessageId(sysMessage.getId());
                userMessage.setUserId(user.getId());
                userMessage.setReadStatus(0);
                userMessageService.save(userMessage);
            }
        }else {

            sysMessage.setSenderId(realUserId);
            sysMessage.setContent(content);
            sysMessage.setTitle(title);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(pushTime);
            sysMessage.setPushTime(date);
            if(messageType == null || messageType.isEmpty()){
                messageType=("其他消息");
            }
            Long messageTypeId = messageTypeService.getOne(Wrappers.<MessageType>lambdaQuery().eq(MessageType::getTypeName,messageType)).getId();
            sysMessage.setMessageTypeId(messageTypeId);
            sysMessage.setPushScope(1);
            //设置推送状态
            sysMessage.setPushStatus(2);
            //过期时间是否需要设计

            //保存后台管理员发送的消息信息
            messageService.save(sysMessage);

            //保存前台用户接收的消息信息
            UserMessage userMessage = new UserMessage();
            userMessage.setMessageId(sysMessage.getId());
            userMessage.setUserId(Long.valueOf(userId));
            userMessage.setReadStatus(0);
            userMessageService.save(userMessage);
        }
        // 这里可以尝试再次发送或记录日志等操作
        rabbitTemplate.convertAndSend(RabbitMQConfig.AUDIT_EXCHANGE, "", body);


    }

    // ============================================================
    // ⑤ 离线消息队列消费
    // ============================================================

    /**
     * 监听离线消息队列
     */
    @RabbitListener(queues = RabbitMQConfig.OFFLINE_MESSAGE_QUEUE, containerFactory = "manualAckContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleOfflineMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[离线消息] 收到离线消息: {}", body);

            // 解析消息内容
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            String userId = jsonObject.getString("userId");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");

            // 模拟业务处理
            Thread.sleep(200);

            // 手动 ACK 确认
            channel.basicAck(deliveryTag, false);

            // 处理离线消息（例如存储到数据库，等待用户上线后推送）
            MessageChatEndpoint.sendOfflineMessage(jsonObject);
            
            log.info("离线消息处理完成: {}", body);

        } catch (Exception e) {
            log.error("离线消息处理失败: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false); // 拒绝并不重回队列，触发备份交换机
        }
    }
}