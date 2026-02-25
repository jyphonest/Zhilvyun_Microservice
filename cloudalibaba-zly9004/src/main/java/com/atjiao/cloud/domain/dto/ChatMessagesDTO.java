package com.atjiao.cloud.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ypJiao
 * * @data 2025/12/29 14:56
 * @description: TODO
 **/
@Data
public class ChatMessagesDTO {

    private Long messageId;

    /**
     * 所属会话id
     */
    private Long sessionId;

    /**
     * 发送者id
     */
    private Long senderId;

    /**
     * 接收者id（用于创建新会话时指定对方，如果是用户发送则为客服ID，如果是客服发送则为用户ID）
     */
    private Long receiverId;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型（1为文本 2为图片 3为系统提示）
     */
    private Integer messageType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 已读状态（1为未读 2为已读）
     */
    private Integer readStatus;

    /**
     * 是否撤回（1为未撤回 2为撤回）
     */
    private Integer isRevoked;
}
