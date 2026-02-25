package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @TableName message_type
 */
@Data
public class MessageType extends  BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String typeName;

    private String icon;

    private Integer sort;

    private Integer type;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Integer delFlag;
}