package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 旅游计划详情表
 * @TableName travel_plan_daily
 */
@Data
public class TravelPlanDaily extends BaseEntity{
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联的旅游计划id
     */
    private Long planId;

    /**
     * 第几天
     */
    private Integer dayNumber;

    /**
     * 游玩攻略描述
     */
    private String travelDescription;

    /**
     * 游玩攻略路线
     */
    private String travelRoute;

    /**
     * 交通攻略描述
     */
    private String transportationDescription;

    /**
     * 餐饮攻略描述
     */
    private String diningDescription;

    /**
     * 住宿攻略描述
     */
    private String accommodateDescription;
}