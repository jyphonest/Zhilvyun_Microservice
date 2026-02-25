package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.ChatMessages;
import com.atjiao.cloud.domain.vo.ChatSessionsVo;
import com.atjiao.cloud.mapper.ChatMessagesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.ChatSessions;
import com.atjiao.cloud.service.ChatSessionsService;
import com.atjiao.cloud.mapper.ChatSessionsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【chat_sessions(聊天会话表)】的数据库操作Service实现
* @createDate 2025-09-09 16:34:31
*/
@Service
@RequiredArgsConstructor
public class ChatSessionsServiceImpl extends ServiceImpl<ChatSessionsMapper, ChatSessions>
    implements ChatSessionsService{

    private final  ChatSessionsMapper chatSessionsMapper;

    private final ChatMessagesMapper chatMessagesMapper;

    // 移除 ChatMessagesService 依赖

    /**
     * 创建聊天会话
     * @param userId
     * @param staffId
     * @return
     */
    @Override
    public ChatSessions createSession(Long userId, Long staffId) {
        ChatSessions chatSessions = new ChatSessions();
        chatSessions.setUserId(userId);
        chatSessions.setStaffId(staffId);
        chatSessions.setStatus(2); // 进行中
        chatSessions.setStartTime(new Date());
        chatSessions.setEndTime(null);
        chatSessionsMapper.insert(chatSessions);
        return chatSessions;
    }

    @Override
    public void assignStaff(Long sessionId, Long staffId) {
        ChatSessions chatSessions = new ChatSessions();
        chatSessions.setSessionId(String.valueOf(sessionId));
        chatSessions.setStaffId(staffId);
        chatSessionsMapper.updateById(chatSessions);
    }

    @Override
    public List<ChatSessions> getSessionList(Long userId) {
        return this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChatSessions>()
                .eq(ChatSessions::getUserId, userId)
                .orderByDesc(ChatSessions::getStartTime));
    }

    /**
     * 结束当前聊天会话
     * @param sessionId
     */
    @Override
    public void endSession(Long sessionId) {
        // 查询该会话的最后一条消息ID
        ChatMessages lastMessage = chatMessagesMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChatMessages>()
                .eq(ChatMessages::getSessionId, sessionId)
                .orderByDesc(ChatMessages::getSendTime)
                .last("LIMIT 1")
        );
        
        ChatSessions chatSessions = new ChatSessions();
        chatSessions.setSessionId(String.valueOf(sessionId));
        chatSessions.setStatus(3);
        chatSessions.setEndTime(new Date());
        if (lastMessage != null) {
            chatSessions.setLastMessageId(lastMessage.getMessageId());
        }
        chatSessionsMapper.updateById(chatSessions);
    }

    /**
     * 获取会话详情方法
     * @param sessionId
     * @return
     */
    @Override
    public ChatSessionsVo getSessionDetails(Long sessionId) {
        ChatSessions chatSessions = new ChatSessions();
        chatSessions=chatSessionsMapper.selectById(sessionId);
        ChatSessionsVo chatSessionsVo = new ChatSessionsVo();
        BeanUtils.copyProperties(chatSessions,chatSessionsVo);
        /**
         * 查询sessionId对应的消息列表
         */
        List<ChatMessages> chatMessages = chatMessagesMapper.selectList(
            new LambdaQueryWrapper<ChatMessages>()
                .eq(ChatMessages::getSessionId, sessionId)
                .eq(ChatMessages::getIsRevoked, 1) // 过滤未撤回的消息
                .orderByAsc(ChatMessages::getSendTime) // 按时间升序排序
        );
        chatSessionsVo.setChatMessages(chatMessages);

        return chatSessionsVo;
    }
}




