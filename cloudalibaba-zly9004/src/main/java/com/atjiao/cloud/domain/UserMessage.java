package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @TableName user_message
 */
@Data
@TableName("user_message")
public class UserMessage extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long messageId;

    private Long userId;

    private Integer readStatus;

    private Integer sendStatus;

    private Date readTime;
}