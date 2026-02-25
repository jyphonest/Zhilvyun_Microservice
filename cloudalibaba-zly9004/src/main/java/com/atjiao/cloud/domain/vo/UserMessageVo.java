package com.atjiao.cloud.domain.vo;

import com.atjiao.cloud.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 焦叶鹏
 * * @data 2025/10/24 14:48
 * @description: TODO
 **/

@Data
public class UserMessageVo extends BaseEntity {

    private Long id;

    private Long messageId;

    private Long userId;

    private Integer readStatus;

    private Integer sendStatus;

    private Date readTime;

    private String title;

    private String content;

    private Long senderId;

    private Integer pushScope;

    private Long messageTypeId;

    private Date pushTime;

    private Integer pushStatus;

    private Date expireTime;
    /**
     * 消息类型
     */
    private String messageType;
}
