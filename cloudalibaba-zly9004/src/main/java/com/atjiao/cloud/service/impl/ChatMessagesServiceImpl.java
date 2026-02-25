package com.atjiao.cloud.service.impl;

import com.alibaba.fastjson2.JSON;
import com.atjiao.cloud.domain.ChatSessions;
import com.atjiao.cloud.domain.dto.ChatMessagesDTO;
import com.atjiao.cloud.domain.vo.ChatMessagesVo;
import com.atjiao.cloud.rabbitMq.config.RabbitMQChatConfig;
import com.atjiao.cloud.service.ChatSessionsService;
import com.atjiao.cloud.service.UserInformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.ChatMessages;
import com.atjiao.cloud.service.ChatMessagesService;
import com.atjiao.cloud.mapper.ChatMessagesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 焦叶鹏
* @description 针对表【chat_messages(聊天消息表)】的数据库操作Service实现
* @createDate 2025-09-09 16:34:27
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessagesServiceImpl extends ServiceImpl<ChatMessagesMapper, ChatMessages>
    implements ChatMessagesService{

    private final RabbitTemplate rabbitTemplate;
    private final ChatSessionsService chatSessionsService;
    private final UserInformationService userInformationService;

    /**
     * 发送消息方法 - 通过RabbitMQ异步处理
     * 如果sessionId为空，则创建新会话
     */
    @Override
    public void sendMessage(ChatMessagesDTO chatMessagesDTO) {
        // 判断是否需要创建新会话
        if (chatMessagesDTO.getSessionId() == null) {
            // sessionId为空，创建新会话
            if (chatMessagesDTO.getReceiverId() == null) {
                throw new RuntimeException("创建新会话时必须指定接收者ID");
            }
            
            // 根据发送者和接收者创建会话
            // 需要判断哪个是用户，哪个是客服
            // 这里假设：发送者为用户userId，接收者为staffId
            // 实际应用中需要根据业务逻辑判断用户和客服角色
            ChatSessions newSession = chatSessionsService.createSession(
                    chatMessagesDTO.getSenderId(), 
                    chatMessagesDTO.getReceiverId()
            );
            
            // 设置会话sessionId（String转换为Long）
            if (newSession.getSessionId() != null) {
                chatMessagesDTO.setSessionId(Long.valueOf(newSession.getSessionId()));
            }
            
            log.info("创建新会话: sessionId={}, userId={}, staffId={}",
                    newSession.getSessionId(), chatMessagesDTO.getSenderId(), chatMessagesDTO.getReceiverId());
        }
        
        // 将消息发送到RabbitMQ交换机
        String messageJson = JSON.toJSONString(chatMessagesDTO);
        rabbitTemplate.convertAndSend(
                RabbitMQChatConfig.CHAT_MSG_EXCHANGE,
                RabbitMQChatConfig.RK_CHAT_MSG_SEND,
                messageJson
        );
        log.info("聊天消息已发送到MQ: sessionId={}, senderId={}", 
                chatMessagesDTO.getSessionId(), chatMessagesDTO.getSenderId());
    }

    @Override
    public List<ChatMessagesVo> getMessageList(Long sessionId) {

        LambdaQueryWrapper<ChatMessages> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(ChatMessages::getSessionId, sessionId)
                .eq(ChatMessages::getIsRevoked, 1) // 过滤未撤回的消息
                .orderByAsc(ChatMessages::getSendTime); // 按时间升序排序
        return this.list(lambdaQueryWrapper)
                .stream()
                .map(message ->{
                    ChatMessagesVo chatMessagesVo = new ChatMessagesVo();
                    BeanUtils.copyProperties(message, chatMessagesVo);
                    chatMessagesVo.setSenderIdentity(userInformationService.getById(message.getSenderId()).getAuthority());
                    chatMessagesVo.setReceiverIdentity(userInformationService.getById(message.getReceiverId()).getAuthority());
                    return chatMessagesVo;
                }).toList();
    }

    @Override
    public void setMessageRead(Long messageId, Long currentUserId) {
        // 权限验证：只能设置发送给自己的消息为已读（即不能设置自己发送的消息为已读）
        ChatMessages message = this.getById(messageId);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }
        // 使用receiverId字段验证：当前用户必须是消息的接收者
        if (message.getReceiverId() == null || !message.getReceiverId().equals(currentUserId)) {
            throw new RuntimeException("只能设置发送给自己的消息为已读");
        }
        
        this.update(new LambdaUpdateWrapper<ChatMessages>()
                .eq(ChatMessages::getMessageId, messageId)
                .set(ChatMessages::getReadStatus, 2)); // 设置为已读
    }

    @Override
    public void revokeMessage(Long messageId, Long currentUserId) {
        // 权限验证：只能撤回自己发送的消息
        ChatMessages message = this.getById(messageId);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }
        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("只能撤回自己发送的消息");
        }
        
        this.update(new LambdaUpdateWrapper<ChatMessages>()
                .eq(ChatMessages::getMessageId, messageId)
                .set(ChatMessages::getIsRevoked, 2)); // 设置为已撤回
    }

    @Override
    public ChatMessages getMessage(Long messageId) {
        return this.getById(messageId);
    }
}




