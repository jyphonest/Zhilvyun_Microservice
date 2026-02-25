package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @TableName message
 */
@Data
@TableName("message")
public class SysMessage extends  BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private String content;

    private Long senderId;

    private Integer pushScope;

    private Long messageTypeId;

    private Date pushTime;

    private Integer pushStatus;

    private Date expireTime;

    private Date effectiveTime;

    private Integer type;
}