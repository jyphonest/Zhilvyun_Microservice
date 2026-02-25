package com.atjiao.cloud.mapper;

import com.atjiao.cloud.domain.TravelStaytimeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 焦叶鹏
* @description 针对表【travel_staytime_record】的数据库操作Mapper
* @createDate 2025-07-24 09:51:42
* @Entity com.atjiao.cloud.domain.TravelStaytimeRecord
*/
public interface TravelStaytimeRecordMapper extends BaseMapper<TravelStaytimeRecord> {

    /**
     * 统计某计划平均停留时长
     */
    Long getAvgStayTimeByPlanId(Long planId);
}




