package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.TravelPlan;
import com.atjiao.cloud.domain.dto.TravelPlanDTO;
import com.atjiao.cloud.domain.vo.TravelPlanDailyVo;
import com.atjiao.cloud.domain.vo.TravelPlanVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【travel_plan(旅游计划基本信息表)】的数据库操作Service
* @createDate 2025-07-15 21:27:50
*/
public interface TravelPlanService extends IService<TravelPlan> {

    /**
     * 查询单个行程计划基本信息详情
     */
    TravelPlanVo getOneById(Long id);

    /**
     * 查询行程计划基本详情列表
     */
    IPage<TravelPlanVo> queryTravelPlanList(TravelPlanDTO travelPlanDTO,PageQuery pageQuery);

    /**
     * 保存和更新行程计划表
     */
    List<TravelPlanDailyVo> saveTravelPlan(TravelPlanDTO travelPlanDTO);

    /**
     * 删除行程计划表
     */
    Boolean deleteTravelPlan(Long id);

    /**
     * 暂存行程计划表
     */
    Boolean saveTravelPlanStash(TravelPlanDTO travelPlanDTO);

    /**
     * 旅游计划表点赞和取消点赞操作
     */
    Boolean likeTravelPlan(Long planId);

    /**
     * 旅游计划表收藏和取消收藏操作
     */
    Boolean collectTravelPlan(Long planId);

    /**
     * 旅游计划表增加浏览量
     */
    Boolean increasePageViews(Long planId);

    /**
     * 查询收藏的旅游计划列表
     */
    IPage<TravelPlanVo> queryCollectTravelPlanList(PageQuery pageQuery);

    /**
     * 后台审核旅游计划
     */
    boolean auditTravelPlan(Long planId, Integer operationType,String note);

    /**
     * 用户查询自己收藏的旅游计划
     */
    IPage<TravelPlanVo> queryUserCollectTravelPlanList(TravelPlanDTO travelPlanDTO,PageQuery pageQuery);

}
