package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 旅游计收藏记录表
 * @TableName travel_user_favorite
 */
@Data
@TableName(value = "travel_user_favorite")
public class TravelUserFavorite extends  BaseEntity {
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
    private Long userId;

    /**
     * 操作类型（0为收藏 1为取消收藏）
     */
    private Integer operationType;

}