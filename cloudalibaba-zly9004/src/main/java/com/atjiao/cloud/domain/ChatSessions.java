package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 聊天会话表
 * @TableName chat_sessions
 */
@Data
public class ChatSessions extends BaseEntity{
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String sessionId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 客服id
     */
    private Long staffId;

    /**
     * 会话状态（1为未开始 2为进行中 3为已结束）
     */
    private Integer status;

    /**
     * 会话开始时间
     */
    private Date startTime;

    /**
     * 会话结束时间
     */
    private Date endTime;

    /**
     * 最后一条消息id
     */
    private Long lastMessageId;


}