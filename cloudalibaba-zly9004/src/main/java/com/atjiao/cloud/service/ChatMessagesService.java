package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.ChatMessages;
import com.atjiao.cloud.domain.dto.ChatMessagesDTO;
import com.atjiao.cloud.domain.vo.ChatMessagesVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【chat_messages(聊天消息表)】的数据库操作Service
* @createDate 2025-09-09 16:34:27
*/
public interface ChatMessagesService extends IService<ChatMessages> {

    /**
     * 发送消息方法
     */
    void sendMessage(ChatMessagesDTO chatMessagesDTO);

    /**
     * 获取消息列表方法
     */
    List<ChatMessagesVo> getMessageList(Long sessionId);

    /**
     * 设置消息已读方法
     */
    void setMessageRead(Long messageId, Long currentUserId);

    /**
     * 撤回消息方法
     */
    void revokeMessage(Long messageId, Long currentUserId);

    /**
     * 查询消息方法
     */
    ChatMessages getMessage(Long messageId);

}
