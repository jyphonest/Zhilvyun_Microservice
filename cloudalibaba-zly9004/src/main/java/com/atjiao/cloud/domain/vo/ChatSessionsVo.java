package com.atjiao.cloud.domain.vo;

import com.atjiao.cloud.domain.ChatMessages;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/9/12 15:55
 * @description: TODO
 **/
@Data
public class ChatSessionsVo {

    /**
     * 主键id
     */
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

    /**
     * 消息对象
     */
    private List<ChatMessages> chatMessages;

}
