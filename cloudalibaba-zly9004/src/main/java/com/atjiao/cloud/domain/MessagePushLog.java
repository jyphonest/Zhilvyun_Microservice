package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @TableName message_push_log
 */
@Data
public class MessagePushLog extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long messageId;

    private String pushScope;

    private Integer targetCount;

    private Integer successCount;

    private Integer failCount;

    private Integer failReason;

    private Integer operatorId;

    private Date pushTime;
}