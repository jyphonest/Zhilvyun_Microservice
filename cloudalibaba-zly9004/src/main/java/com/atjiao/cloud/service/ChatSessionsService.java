package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.ChatSessions;
import com.atjiao.cloud.domain.vo.ChatSessionsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【chat_sessions(聊天会话表)】的数据库操作Service
* @createDate 2025-09-09 16:34:31
*/
public interface ChatSessionsService extends IService<ChatSessions> {

    /**
     * 创建聊天会话
     */
    ChatSessions createSession(Long userId, Long staffId);

    /**
     * 分配客服方法
     */
    void assignStaff(Long sessionId, Long staffId);

    /**
     * 获取会话列表方法
     */
    List<ChatSessions> getSessionList(Long userId);

    /**
     * 结束会话方法
     */
    void endSession(Long sessionId);

    /**
     * 获取会话详情方法
     */
    ChatSessionsVo getSessionDetails(Long sessionId);

}
