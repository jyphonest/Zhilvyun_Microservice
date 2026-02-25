package com.atjiao.cloud.ws;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.atjiao.cloud.config.GetHttpSessionConfig;
import com.atjiao.cloud.domain.SysMessage;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.UserMessage;
import com.atjiao.cloud.service.MessageService;
import com.atjiao.cloud.service.OfflineMessageService;
import com.atjiao.cloud.service.UserInformationService;
import com.atjiao.cloud.service.UserMessageService;
import com.atjiao.cloud.util.FastJsonUtil;
import com.atjiao.cloud.util.MessageUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 焦叶鹏
 * * @data 2025/10/18 23:27
 * @description: 通知端口，用于处理系统通知消息
 **/
@ServerEndpoint(value = "/message", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class MessageChatEndpoint {

    // 保存在线的用户，key为用户名，value为 Session 对象
    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();
    
    // 注入服务
    private static UserInformationService userInformationService;
    private static UserMessageService userMessageService;
    private static MessageService messageService;
    private static RedisTemplate<String, Object> redisTemplate;
    private static OfflineMessageService offlineMessageService;
    
    @Autowired
    public void setUserInformationService(UserInformationService userInformationService) {
        MessageChatEndpoint.userInformationService = userInformationService;
    }

    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        MessageChatEndpoint.userMessageService = userMessageService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        MessageChatEndpoint.messageService = messageService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        MessageChatEndpoint.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setOfflineMessageService(OfflineMessageService offlineMessageService) {
        MessageChatEndpoint.offlineMessageService = offlineMessageService;
    }

    /**
     * 获取所有在线用户
     * @return 在线用户名集合
     */
    public static Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }

    private HttpSession httpSession;

    /**
     * 建立 websocket 连接后，被调用
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        String user = (String) this.httpSession.getAttribute("currentUser");
        if (user != null) {
            onlineUsers.put(user, session);
            log.info("用户[{}]已上线通知端点，当前在线人数: {}", user, onlineUsers.size());
            
            // 用户上线后，推送离线消息
            pushOfflineMessages(user);
        }
    }
    
    /**
     * 用户上线后，推送离线消息
     * @param username 用户名
     */
    private void pushOfflineMessages(String username) {
        try {
            // 获取用户信息
            UserInformation userInformation = userInformationService.getOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserInformation>lambdaQuery()
                    .eq(UserInformation::getAccountNumber, username));
            
            if (userInformation != null) {
                Long userId = userInformation.getId();
                
                // 获取用户的离线消息ID列表
                List<Long> messageIds = offlineMessageService.getOfflineMessageIds(userId);
                
                if (messageIds != null && !messageIds.isEmpty()) {
                    log.info("用户[{}]有{}条离线消息待推送", username, messageIds.size());
                    
                    // 遍历离线消息ID列表，获取消息内容并推送
                    for (Long messageId : messageIds) {
                        // 获取消息内容
                        JSONObject messageContent = offlineMessageService.getOfflineMessageContent(messageId);
                        
                        if (messageContent != null) {
                            // 推送消息给用户
                            boolean sent = sendNotificationToUser(username, messageContent);
                            
                            if (sent) {
                                // 推送成功，从Redis中删除该消息ID
                                offlineMessageService.removeOfflineMessageId(userId, messageId);
                                
                                // 更新数据库中的发送状态为已发送
                                UserMessage userMessage = userMessageService.getOne(
                                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserMessage>lambdaQuery()
                                        .eq(UserMessage::getMessageId, messageId)
                                        .eq(UserMessage::getUserId, userId)
                                        .orderByDesc(UserMessage::getId)
                                        .last("LIMIT 1"));
                                
                                if (userMessage != null) {
                                    userMessage.setSendStatus(1); // 设置为已发送
                                    userMessageService.updateById(userMessage);
                                }
                                
                                log.info("用户[{}]的离线消息[{}]推送成功", username, messageId);
                            } else {
                                log.warn("用户[{}]的离线消息[{}]推送失败", username, messageId);
                            }
                        } else {
                            log.warn("用户[{}]的离线消息[{}]内容为空", username, messageId);
                        }
                    }
                } else {
                    log.info("用户[{}]没有离线消息", username);
                }
            } else {
                log.warn("未找到用户[{}]的信息", username);
            }
        } catch (Exception e) {
            log.error("推送用户[{}]离线消息失败: {}", username, e.getMessage(), e);
        }
    }

    /**
     * 发送系统通知给指定用户
     * @param username 用户名
     * @param message 消息内容
     * @return 是否发送成功（在线则发送成功，不在线则发送失败）
     */
    public static boolean sendNotificationToUser(String username, Object message) {
        Session session = onlineUsers.get(username);
        if (session != null && session.isOpen()) {
            try {
                String notificationMessage = MessageUtils.getMessage(true, null, message);
                session.getBasicRemote().sendText(notificationMessage);
                log.info("发送系统通知给用户[{}]: {}", username, message);
                return true; // 发送成功
            } catch (Exception e) {
                log.error("发送系统通知给用户[{}]失败: {}", username, e.getMessage(), e);
                return false; // 发送失败
            }
        } else {
            log.warn("用户[{}]不在线或会话已关闭，无法发送通知", username);
            return false; // 用户不在线
        }
    }

    /**
     * 发送系统通知给所有在线用户
     * @param message 消息内容
     */
    public static void broadcastNotification(Object message) {
        try {
            String notificationMessage = MessageUtils.getMessage(true, null, message);
            // 发送给所有在线用户
            for (Map.Entry<String, Session> entry : onlineUsers.entrySet()) {
                Session session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(notificationMessage);
                    } catch (IOException e) {
                        log.error("向用户[{}]发送广播通知失败: {}", entry.getKey(), e.getMessage(), e);
                    }
                }
            }
            log.info("广播系统通知: {}", message);
        } catch (Exception exception) {
            log.error("广播系统通知失败", exception);
        }
    }

    /**
     * 发送离线消息
     * @param message 消息内容
     */
    public static void sendOfflineMessage(Object message) {
        try {
            // 离线消息的处理逻辑
            log.info("处理离线消息: {}", message);
            
            // 1. 持久化到数据库中（参考RabbitMQConsumer类的handleNotificationLog方法）
           // saveOfflineMessageToDatabase(message);
            
            // 2. 存储在Redis缓存中
            saveOfflineMessageToRedis(message);
        } catch (Exception e) {
            log.error("处理离线消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 将离线消息持久化到数据库
     * @param message 消息内容
     */
    private static void saveOfflineMessageToDatabase(Object message) {
        try {
            // 解析消息内容
            JSONObject jsonObject = null;
            if (message instanceof String) {
                jsonObject = FastJsonUtil.parseSafe((String) message);
            } else if (message instanceof JSONObject) {
                jsonObject = (JSONObject) message;
            } else {
                jsonObject = (JSONObject) JSON.toJSON(message);
            }
            
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
                SysMessage sysMessage = new SysMessage();
                sysMessage.setSenderId(realUserId);
                sysMessage.setContent(content);
                sysMessage.setTitle(title);
                // 解析时间
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date date = sdf.parse(pushTime);
                sysMessage.setPushTime(date);
                if(messageType == null || messageType.isEmpty()){
                    messageType=("其他消息");
                }
                // 获取消息类型ID
                // 这里简化处理，实际项目中应该通过服务获取
                sysMessage.setMessageTypeId(1L); // 默认类型ID
                sysMessage.setPushScope(2);
                //设置推送状态
                sysMessage.setPushStatus(1);
                
                //保存后台管理员发送的消息信息
                messageService.save(sysMessage);
    
                //保存前台用户接收的消息信息
                List<UserInformation> userList = userInformationService.list();
                for (UserInformation user : userList) {
                    UserMessage userMessage = new UserMessage();
                    userMessage.setMessageId(sysMessage.getId());
                    userMessage.setUserId(user.getId());
                    userMessage.setReadStatus(0);
                    userMessage.setSendStatus(0); // 离线消息，发送状态为未发送
                    userMessageService.save(userMessage);
                }
            }else {
                SysMessage sysMessage = new SysMessage();
                sysMessage.setSenderId(realUserId);
                sysMessage.setContent(content);
                sysMessage.setTitle(title);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date date = sdf.parse(pushTime);
                sysMessage.setPushTime(date);
                if(messageType == null || messageType.isEmpty()){
                    messageType=("其他消息");
                }
                // 获取消息类型ID
                // 这里简化处理，实际项目中应该通过服务获取
                sysMessage.setMessageTypeId(1L); // 默认类型ID
                sysMessage.setPushScope(1);
                //设置推送状态
                sysMessage.setPushStatus(1);
                
                //保存后台管理员发送的消息信息
                messageService.save(sysMessage);
    
                //保存前台用户接收的消息信息
                UserMessage userMessage = new UserMessage();
                userMessage.setMessageId(sysMessage.getId());
                userMessage.setUserId(Long.valueOf(userId));
                userMessage.setReadStatus(0);
                userMessage.setSendStatus(0); // 离线消息，发送状态为未发送
                userMessageService.save(userMessage);
            }
            log.info("离线消息已持久化到数据库");
        } catch (Exception e) {
            log.error("离线消息持久化到数据库失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 将离线消息存储到Redis缓存
     * @param message 消息内容
     */
    private static void saveOfflineMessageToRedis(Object message) {
        try {
            // 解析消息内容
            JSONObject jsonObject = null;
            if (message instanceof String) {
                jsonObject = FastJsonUtil.parseSafe((String) message);
            } else if (message instanceof JSONObject) {
                jsonObject = (JSONObject) message;
            } else {
                jsonObject = (JSONObject) JSON.toJSON(message);
            }
            
            String userId = jsonObject.getString("userId");
            String content = jsonObject.getString("content");
            String title = jsonObject.getString("title");
            String pushTime = jsonObject.getString("pushTime");
            String messageType = jsonObject.getString("messageType");
            Long realUserId = jsonObject.getLong("realUserId");
            
            // 获取消息ID-
            Long messageId = null;
            if(userId.equals("all")){
                // 全体用户推送，获取最新的一条系统消息
                SysMessage sysMessage = messageService.getOne(Wrappers.<SysMessage>lambdaQuery()
                        .orderByDesc(SysMessage::getCreateTime)
                        .last("LIMIT 1")
                );
                if (sysMessage != null) {
                    messageId = sysMessage.getId();
                }
            } else {
                // 单个用户推送，获取最新的一条系统消息
                SysMessage sysMessage = messageService.getOne(Wrappers.<SysMessage>lambdaQuery()
                        .orderByDesc(SysMessage::getCreateTime)
                        .last("LIMIT 1")
                );
                if (sysMessage != null) {
                    messageId = sysMessage.getId();
                }
            }
            
            if (messageId != null) {
                // Redis缓存的存储结构
                // List 结构：存储用户离线消息的消息 ID 列表（按时间顺序，左进右出）
                // 键名格式：offline:msg:ids:{user_id}
                // Hash 结构：存储消息 ID 与消息内容的映射
                // 键名固定为 offline:msg:content，field 为消息 ID，value 为消息 JSON 字符串
                
                if(userId.equals("all")){
                    // 全体用户推送，需要为每个用户都存储离线消息
                    List<UserInformation> userList = userInformationService.list();
                    for (UserInformation user : userList) {
                        Long userIdValue = user.getId();
                        String userIdStr = String.valueOf(userIdValue);
                        
                        // 将消息ID添加到用户离线消息列表中（左进右出）
                        String listKey = "offline:msg:ids:" + userIdStr;
                        redisTemplate.opsForList().leftPush(listKey, messageId);
                        
                        // 将消息内容存储到Hash结构中
                        String hashKey = "offline:msg:content";
                        String messageJson = JSON.toJSONString(jsonObject);
                        redisTemplate.opsForHash().put(hashKey, String.valueOf(messageId), messageJson);
                    }
                } else {
                    // 单个用户推送

                    // 将消息ID添加到用户离线消息列表中（左进右出）
                    String listKey = "offline:msg:ids:" + userId;
                    redisTemplate.opsForList().leftPush(listKey, messageId);
                    
                    // 将消息内容存储到Hash结构中
                    String hashKey = "offline:msg:content";
                    String messageJson = JSON.toJSONString(jsonObject);
                    redisTemplate.opsForHash().put(hashKey, String.valueOf(messageId), messageJson);
                }
                
                log.info("离线消息已存储到Redis缓存");
            } else {
                log.warn("无法获取消息ID，离线消息未存储到Redis缓存");
            }
        } catch (Exception e) {
            log.error("离线消息存储到Redis缓存失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 断开 websocket 连接时被调用
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String user = (String) this.httpSession.getAttribute("currentUser");
        if (user != null) {
            Session removed = onlineUsers.remove(user);
            if (removed != null) {
                removed.close();
            }
            session.close();
            log.info("用户[{}]已下线通知端点，当前在线人数: {}", user, onlineUsers.size());
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket通知端点发生错误", error);
    }
}