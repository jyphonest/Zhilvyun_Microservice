package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @TableName goods_operation_record
 */
@Data
public class GoodsOperationRecord extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long goodsId;

    private Long usreId;

    private Integer operationType;

    private String operationInfo;

    private String note;
}