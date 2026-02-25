package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 焦叶鹏
 * * @data 2025/7/19 19:11
 * @description: TODO
 **/
@Data
public class TravelPlanDailyDTO extends BaseEntity {
    /**
     * 主键id
     */
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
