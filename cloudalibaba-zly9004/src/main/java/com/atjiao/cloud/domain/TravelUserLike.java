package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

/**
 * 旅游计划点赞记录表
 * @TableName travel_user_like
 */
@Data
@TableName(value = "travel_user_like")
public class TravelUserLike extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 旅游计划表id
     */
    private Long planId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 操作类型（0为点赞 1为取消点赞）
     */
    private Integer operationType;

}