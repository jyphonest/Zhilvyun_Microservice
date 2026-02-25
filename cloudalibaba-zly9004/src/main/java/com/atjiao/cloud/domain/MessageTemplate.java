package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @TableName message_template
 */
@Data
public class MessageTemplate extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private String content;

    private Long messageTypeId;

    private Integer templateType;

    private Long version;

    private String attachmentUrl;

    private String type;
}