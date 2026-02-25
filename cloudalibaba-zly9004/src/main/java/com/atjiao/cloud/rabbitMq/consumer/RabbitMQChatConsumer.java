package com.atjiao.cloud.rabbitMq.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.atjiao.cloud.domain.ChatMessages;
import com.atjiao.cloud.domain.ChatSessions;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.dto.ChatMessagesDTO;
import com.atjiao.cloud.mapper.ChatMessagesMapper;
import com.atjiao.cloud.mapper.ChatSessionsMapper;
import com.atjiao.cloud.rabbitMq.config.RabbitMQChatConfig;
import com.atjiao.cloud.service.UserInformationService;
import com.atjiao.cloud.util.FastJsonUtil;
import com.atjiao.cloud.ws.ChatEndpoint;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ypJiao
 * @data 2025/12/29 15:15
 * @description: RabbitMQ聊天消息消费者
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMQChatConsumer {

    private final ChatMessagesMapper chatMessagesMapper;
    private final ChatSessionsMapper chatSessionsMapper;
    private final UserInformationService userInformationService;

    /**
     * 消费持久化队列，将消息保存到数据库
     */
    @RabbitListener(queues = RabbitMQChatConfig.CHAT_MSG_PERSIST_QUEUE, containerFactory = "manualAckContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void handleChatMessagePersist(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[持久化队列] 收到聊天消息: {}", body);

            // 解析消息
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            ChatMessagesDTO chatMessagesDTO = jsonObject.toJavaObject(ChatMessagesDTO.class);

            // 确定接收者ID（如果DTO中没有，则通过会话信息获取）
            Long receiverId = chatMessagesDTO.getReceiverId();
            if (receiverId == null && chatMessagesDTO.getSessionId() != null) {
                // 通过会话信息确定接收者
                ChatSessions session = chatSessionsMapper.selectById(chatMessagesDTO.getSessionId());
                if (session != null) {
                    if (chatMessagesDTO.getSenderId().equals(session.getUserId())) {
                        receiverId = session.getStaffId();
                    } else {
                        receiverId = session.getUserId();
                    }
                }
            }

            // 构造消息实体
            ChatMessages chatMessage = new ChatMessages();
            chatMessage.setSessionId(chatMessagesDTO.getSessionId());
            chatMessage.setSenderId(chatMessagesDTO.getSenderId());
            chatMessage.setReceiverId(receiverId); // 设置接收者ID
            chatMessage.setContent(chatMessagesDTO.getContent());
            chatMessage.setMessageType(chatMessagesDTO.getMessageType() != null ? chatMessagesDTO.getMessageType() : 1); // 默认文本消息
            chatMessage.setSendTime(new Date());
            chatMessage.setReadStatus(1); // 未读
            chatMessage.setIsRevoked(1); // 未撤回

            // 保存到数据库
            chatMessagesMapper.insert(chatMessage);
            log.info("[持久化队列] 消息保存成功: messageId={}, sessionId={}, senderId={}, receiverId={}",
                    chatMessage.getMessageId(), chatMessage.getSessionId(), chatMessage.getSenderId(), chatMessage.getReceiverId());

            // 在事务提交后手动确认消息
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        channel.basicAck(deliveryTag, false);
                        log.info("[持久化队列] 消息确认成功，deliveryTag: {}", deliveryTag);
                    } catch (IOException e) {
                        log.error("[持久化队列] 消息确认失败，deliveryTag: {}", deliveryTag, e);
                    }
                }
            });

        } catch (Exception e) {
            log.error("[持久化队列] 消息处理失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false); // 拒绝并不重回队列，触发死信队列
                log.info("[持久化队列] 消息拒绝确认，deliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                log.error("[持久化队列] 消息拒绝确认失败，deliveryTag: {}", deliveryTag, ioException);
            }
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    /**
     * 消费实时推送队列，通过WebSocket推送消息给用户
     */
    @RabbitListener(queues = RabbitMQChatConfig.CHAT_MSG_PUSH_QUEUE, containerFactory = "manualAckContainerFactory")
    public void handleChatMessagePush(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[推送队列] 收到聊天消息: {}", body);

            // 解析消息
            JSONObject jsonObject = FastJsonUtil.parseSafe(body);
            ChatMessagesDTO chatMessagesDTO = jsonObject.toJavaObject(ChatMessagesDTO.class);

            // 确定接收方ID（优先使用DTO中的receiverId，如果没有则通过会话信息获取）
            Long receiverId = chatMessagesDTO.getReceiverId();
            if (receiverId == null) {
                // 通过sessionId获取会话信息，确定接收方
                ChatSessions session = chatSessionsMapper.selectById(chatMessagesDTO.getSessionId());
                if (session == null) {
                    log.warn("[推送队列] 会话不存在: sessionId={}", chatMessagesDTO.getSessionId());
                    channel.basicAck(deliveryTag, false);
                    return;
                }

                // 确定接收方：如果发送者是用户，则接收方是客服；反之亦然
                if (chatMessagesDTO.getSenderId().equals(session.getUserId())) {
                    // 发送者是用户，接收方是客服
                    receiverId = session.getStaffId();
                } else {
                    // 发送者是客服，接收方是用户
                    receiverId = session.getUserId();
                }
            }

            // 获取接收方用户信息
            UserInformation receiver = userInformationService.getById(receiverId);
            if (receiver == null) {
                log.warn("[推送队列] 接收方用户不存在: receiverId={}", receiverId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 获取发送方用户信息
            UserInformation sender = userInformationService.getById(chatMessagesDTO.getSenderId());
            String senderName = sender != null ? sender.getAccountNumber() : "Unknown";

            // 构造WebSocket消息格式
            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("system", false);
            wsMessage.put("fromName", senderName);
            wsMessage.put("message", chatMessagesDTO.getContent());
            wsMessage.put("sessionId", chatMessagesDTO.getSessionId());
            wsMessage.put("messageType", chatMessagesDTO.getMessageType());
            wsMessage.put("sendTime", new Date());
            
            String messageJson = JSON.toJSONString(wsMessage);

            // 通过WebSocket推送消息
            String receiverUsername = receiver.getAccountNumber();
            boolean sent = ChatEndpoint.sendMessageToUser(receiverUsername, messageJson);
            
            if (sent) {
                log.info("[推送队列] 消息已推送给用户[{}]: sessionId={}, senderId={}",
                        receiverUsername, chatMessagesDTO.getSessionId(), chatMessagesDTO.getSenderId());
            } else {
                log.warn("[推送队列] 用户[{}]不在线，消息未推送", receiverUsername);
            }

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("[推送队列] 消息确认成功，deliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            log.error("[推送队列] 消息处理失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false); // 拒绝并不重回队列，触发死信队列
                log.info("[推送队列] 消息拒绝确认，deliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                log.error("[推送队列] 消息拒绝确认失败，deliveryTag: {}", deliveryTag, ioException);
            }
        }
    }

    /**
     * 消费死信队列，处理失败的消息
     */
    @RabbitListener(queues = RabbitMQChatConfig.CHAT_MSG_DEAD_QUEUE)
    public void handleChatMessageDead(Message message) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.warn("[死信队列] 收到失败消息: {}", body);

            // TODO: 这里可以记录到数据库或者发送告警
            // 例如：保存到错误日志表、发送邮件/短信通知等
            
        } catch (Exception e) {
            log.error("[死信队列] 消息处理失败: {}", e.getMessage(), e);
        }
    }
}
