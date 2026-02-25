package com.atjiao.cloud.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author ypJiao
 * * @data 2025/12/30 09:07
 * @description: TODO
 **/
@Data
public class ChatMessagesVo {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /**
     * 已读状态（1为未读 2为已读）
     */
    private Integer readStatus;

    /**
     * 是否撤回（1为未撤回 2为撤回）
     */
    private Integer isRevoked;

    /**
     * 发送者身份
     */
    private String senderIdentity;

    /**
     * 接收者身份
     */
    private String receiverIdentity;
}
