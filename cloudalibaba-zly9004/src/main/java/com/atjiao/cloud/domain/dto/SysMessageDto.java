package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.prefs.BackingStoreException;

/**
 * @author 焦叶鹏
 * * @data 2025/11/19 22:16
 * @description: TODO
 **/
@Data
public class SysMessageDto extends BaseEntity {
    private Long id;

    private String title;

    private String content;

    private String messageTypeName;

    private Long senderId;

    private Integer pushScope;

    private Long messageTypeId;

    private Date pushTime;

    private Integer pushStatus;

    private Date expireTime;

    private Date effectiveTime;

    private Integer type;
}
