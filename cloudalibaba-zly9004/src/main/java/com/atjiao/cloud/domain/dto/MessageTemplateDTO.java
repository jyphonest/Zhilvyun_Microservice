package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/11/21 11:09
 * @description: 消息模板实体类Dto层
 **/
@Data
public class MessageTemplateDTO extends BaseEntity {
    private Long id;

    private String title;

    private String content;

    private Long messageTypeId;

    private Integer templateType;

    private Long version;

    private String attachmentUrl;

    private String type;

    private String messageTypeName;

}
