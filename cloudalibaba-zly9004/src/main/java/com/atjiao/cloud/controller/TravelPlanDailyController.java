package com.atjiao.cloud.controller;

import com.atjiao.cloud.mapper.TravelPlanDailyMapper;
import com.atjiao.cloud.service.TravelPlanDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atjiao.cloud.domain.TravelPlanDaily;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/5 20:27
 * @description: TODO
 **/
@RestController
@RequestMapping("/travelPlanMessage")
@RequiredArgsConstructor
public class TravelPlanDailyController {

    private final TravelPlanDailyMapper travelPlanDailyMapper;
    private final TravelPlanDailyService travelPlanDailyService;

    /**
     * 新增旅游计划详情
     */
    @PostMapping("/create")
    public boolean create(@RequestBody TravelPlanDaily travelPlanDaily) {
        return travelPlanDailyService.createTravelPlanDaily(travelPlanDaily);
    }

    /**
     * 根据id查询旅游计划详情
     */
    @GetMapping("/get/{id}")
    public TravelPlanDaily getById(@PathVariable Long id) {
        return travelPlanDailyService.getTravelPlanDailyById(id);
    }

    /**
     * 查询旅游计划详情列表
     */
    @PostMapping("/list")
    public List<TravelPlanDaily> list(@RequestBody(required = false) TravelPlanDaily query) {
        return travelPlanDailyService.listTravelPlanDaily(query);
    }

    /**
     * 更新旅游计划详情
     */
    @PostMapping("/update")
    public boolean update(@RequestBody TravelPlanDaily travelPlanDaily) {
        return travelPlanDailyService.updateTravelPlanDaily(travelPlanDaily);
    }

    /**
     * 根据id删除旅游计划详情
     */
    @PostMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return travelPlanDailyService.deleteTravelPlanDaily(id);
    }
}
