package com.atjiao.cloud.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/23 20:57
 * @description: TODO
 **/
@Data
public class TravelPlanDailyVo {

    private Long id;

    private List<Long> ids;


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

    private List<SysOssVo> sysOssVos;
}
