package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 聊天消息表
 * @TableName chat_messages
 */
@Data
public class ChatMessages extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long messageId;

    /**
     * 所属会话id
     */
    private Long sessionId;

    /**
     * 发送者id
     */
    private Long senderId;

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