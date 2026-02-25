package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.SysMessage;
import com.atjiao.cloud.domain.vo.UserMessageVo;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.service.MessageService;
import com.atjiao.cloud.service.MessageTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.UserMessage;
import com.atjiao.cloud.service.UserMessageService;
import com.atjiao.cloud.mapper.UserMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author 86182
* @description 针对表【user_message(用户-消息关联表)】的数据库操作Service实现
* @createDate 2025-10-16 18:56:49
*/
@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage>
    implements UserMessageService{

    private  final MessageService messageService;
    private  final MessageTypeService messageTypeService;
    
    /**
     * 更新阅读状态
     * @param Id
     */
    @Override
    public void updateReadStatus(Long Id) {
        UserMessage userMessage = new UserMessage();
        userMessage.setId(Id);
        userMessage.setReadStatus(1); // 已读状态设置为 1
        this.updateById(userMessage);

    }

    /**
     * 更新发送状态
     * @param userMessageId 用户消息ID
     * @param sendStatus 发送状态 (1:已发送, 0:未发送)
     */
    @Override
    public void updateSendStatus(Long userMessageId, Integer sendStatus) {
        UserMessage userMessage = this.getById(userMessageId);
        if (userMessage != null) {
            userMessage.setSendStatus(sendStatus);
            this.updateById(userMessage);
        }
    }

    /**
     * 用户查询自己的消息信息
     */
    @Override
    public List<UserMessageVo> queryMessageInfo() {
        // 获取当前登录用户ID
        Long userId = LoginHelper.getUserId();
        
        // 1. 查询用户关联的消息记录
        List<UserMessage> userMessages = this.list(
                new QueryWrapper<UserMessage>()
                        .eq("user_id", userId));
        
        // 如果没有消息记录，直接返回空列表
        if (userMessages.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 提取消息ID列表
        List<Long> messageIds = userMessages.stream()
                .map(UserMessage::getMessageId)
                .distinct()
                .collect(Collectors.toList());
        
        // 3. 批量查询消息详情
        List<SysMessage> sysMessages = messageService.list(
                new QueryWrapper<SysMessage>()
                        .in("id", messageIds));
        
        // 4. 构建消息ID到消息详情的映射
        Map<Long, SysMessage> messageMap = sysMessages.stream()
                .collect(Collectors.toMap(SysMessage::getId, msg -> msg));
        
        // 5. 提取消息类型ID列表
        List<Long> messageTypeIds = sysMessages.stream()
                .map(SysMessage::getMessageTypeId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        // 6. 批量查询消息类型
        List<MessageType> messageTypes = new ArrayList<>();
        if (!messageTypeIds.isEmpty()) {
            messageTypes = messageTypeService.list(
                    new QueryWrapper<MessageType>()
                            .in("id", messageTypeIds));
        }
        
        // 7. 构建消息类型ID到消息类型的映射
        Map<Long, MessageType> messageTypeMap = messageTypes.stream()
                .collect(Collectors.toMap(MessageType::getId, type -> type));
        
        // 8. 组装结果
        List<UserMessageVo> result = new ArrayList<>();
        for (UserMessage userMessage : userMessages) {
            UserMessageVo vo = new UserMessageVo();
            // 复制用户消息的基本属性
            BeanUtils.copyProperties(userMessage, vo);
            
            // 获取消息详情
            SysMessage sysMessage = messageMap.get(userMessage.getMessageId());
            if (sysMessage != null) {
                // 复制消息详情的属性
                BeanUtils.copyProperties(sysMessage, vo);
                
                // 设置消息类型名称
                MessageType messageType = messageTypeMap.get(sysMessage.getMessageTypeId());
                if (messageType != null) {
                    vo.setMessageType(messageType.getTypeName());
                }
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public void deleteMessage(Long id) {
        this.removeById(id);
    }

    /**
     * 批量删除消息
     * @param ids 消息ID列表
     */
    @Override
    public void deleteMessages(List<Long> ids) {
        // 获取当前登录用户ID
        Long userId = LoginHelper.getUserId();
        
        // 查询要删除的消息是否属于当前用户
        List<UserMessage> userMessages = this.list(
                new QueryWrapper<UserMessage>()
                        .eq("user_id", userId)
                        .in("id", ids));
        
        // 提取实际属于当前用户的ID列表
        List<Long> userMessageIds = userMessages.stream()
                .map(UserMessage::getId)
                .collect(Collectors.toList());
        
        // 执行批量删除
        if (!userMessageIds.isEmpty()) {
            this.removeByIds(userMessageIds);
        }
    }
}