package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 旅游计划审核记录表
 * @TableName travel_operation_record
 */
@Data
public class TravelOperationRecord extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 旅游计划id
     */
    private Long planId;

    /**
     * 用户id
     */
    private Long userid;

    /**
     * 操作类型:1|通过、2|未通过、3|发布招计划、4|暂停、5|开始、6|计划到期
     */
    private Integer operationType;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 操作说明
     */
    private String operationInfo;

}