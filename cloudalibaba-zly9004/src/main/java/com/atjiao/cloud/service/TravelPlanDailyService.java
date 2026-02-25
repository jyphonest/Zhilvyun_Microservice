package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.TravelPlanDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【travel_plan_daily(旅游计划详情表)】的数据库操作Service
* @createDate 2025-07-15 21:28:32
*/
public interface TravelPlanDailyService extends IService<TravelPlanDaily> {

    /**
     * 新增旅游计划详情
     */
    boolean createTravelPlanDaily(TravelPlanDaily travelPlanDaily);

    /**
     * 根据id查询旅游计划详情
     */
    TravelPlanDaily getTravelPlanDailyById(Long id);

    /**
     * 查询旅游计划详情列表
     */
    List<TravelPlanDaily> listTravelPlanDaily(TravelPlanDaily query);

    /**
     * 更新旅游计划详情
     */
    boolean updateTravelPlanDaily(TravelPlanDaily travelPlanDaily);

    /**
     * 根据id删除旅游计划详情
     */
    boolean deleteTravelPlanDaily(Long id);

}
