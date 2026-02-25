package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.TravelPlan;
import com.atjiao.cloud.domain.dto.TravelPlanDTO;
import com.atjiao.cloud.domain.vo.TravelPlanDailyVo;
import com.atjiao.cloud.domain.vo.TravelPlanVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.TravelPlanService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/5 20:26
 * @description: TODO
 **/
@RestController
@RequestMapping("/travelPlanDetail")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    /**
     * 1-> 查询单个行程计划详情
     */
    @GetMapping("/getOneById")
    public ResultData<TravelPlanVo> getOneById(@NotBlank(message = "旅游计划表id不能为空") Long id) {
        return ResultData.success(travelPlanService.getOneById(id));
    }

    /**
     * 2-> 条件查询行程计划列表
     */
    @PostMapping("/queryTravelPlanList")
    public ResultData<IPage<TravelPlanVo>> queryTravelPlanList(@RequestBody TravelPlanDTO travelPlanDTO,PageQuery pageQuery) {
        return ResultData.success(travelPlanService.queryTravelPlanList(travelPlanDTO,pageQuery));
    }

    /**
     * 3-> 保存和更新行程计划表
     */
    @PostMapping("/saveTravelPlan")
    public ResultData<List<TravelPlanDailyVo>> saveTravelPlan(@RequestBody TravelPlanDTO travelPlanDTO) {
        return ResultData.success(travelPlanService.saveTravelPlan(travelPlanDTO));
    }

    /**
     * 4-> 删除行程计划表
     */
    @DeleteMapping("/deleteTravelPlan/{id}")
    public ResultData<Boolean> deleteTravelPlan(@PathVariable Long id) {
        return ResultData.success(travelPlanService.deleteTravelPlan(id));
    }

    /**
     * 5-> 暂存行程计划表
     */
    @PostMapping("/saveTravelPlanStash")
    public ResultData<Boolean> saveTravelPlanStash(@RequestBody TravelPlanDTO travelPlanDTO) {
        return ResultData.success(travelPlanService.saveTravelPlanStash(travelPlanDTO));
    }

    /**
     * 6-> 旅游计划表点赞和取消点赞操作
     */
    @PostMapping("/likeTravelPlan/{planId}")
    public ResultData<Boolean> likeTravelPlan(@PathVariable  @NotNull Long planId) {
        return ResultData.success(travelPlanService.likeTravelPlan(planId));
    }

    /**
     * 7-> 旅游计划表收藏和取消收藏操作
     */
    @PostMapping("/collectTravelPlan/{planId}")
    public ResultData<Boolean> collectTravelPlan(@PathVariable @NotNull Long planId) {
        return ResultData.success(travelPlanService.collectTravelPlan(planId));
    }

    /**
     * 8->旅游计划表增加浏览量
     */
    @PostMapping("/increasePageViews/{planId}")
    public ResultData<Boolean> increasePageViews(@PathVariable @NotNull Long planId) {
        return ResultData.success(travelPlanService.increasePageViews(planId));
    }

    /**
     * 9-> 查询自己发布的旅游计划列表
     */
    @PostMapping("/queryCollectTravelPlanList")
    public ResultData<IPage<TravelPlanVo>> queryCollectTravelPlanList(PageQuery pageQuery) {
        return ResultData.success(travelPlanService.queryCollectTravelPlanList(pageQuery));
    }

    /**
     * 10-> 审核旅游计划表
     */
    @PostMapping("/auditTravelPlan")
    public ResultData<Boolean> auditTravelPlan(@RequestParam Long planId, @RequestParam Integer operationType,@RequestParam String note) {
        return ResultData.success(travelPlanService.auditTravelPlan(planId,operationType,note));
    }


    /**
     * 用户查询自己收藏的旅游计划
     */
    @PostMapping("/queryUserCollectTravelPlanList")
    public ResultData<IPage<TravelPlanVo>> queryUserCollectTravelPlanList(@RequestBody TravelPlanDTO travelPlanDTO,PageQuery pageQuery) {
        return ResultData.success(travelPlanService.queryUserCollectTravelPlanList(travelPlanDTO,pageQuery));
    }

}
