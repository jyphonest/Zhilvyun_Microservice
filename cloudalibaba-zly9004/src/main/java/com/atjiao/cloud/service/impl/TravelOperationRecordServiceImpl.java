package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.TravelPlan;
import com.atjiao.cloud.helper.LoginHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.TravelOperationRecord;
import com.atjiao.cloud.service.TravelOperationRecordService;
import com.atjiao.cloud.mapper.TravelOperationRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author 焦叶鹏
* @description 针对表【travel_operation_record(旅游计划审核记录表)】的数据库操作Service实现
* @createDate 2025-07-22 22:40:50
*/
@Service
@RequiredArgsConstructor
public class TravelOperationRecordServiceImpl extends ServiceImpl<TravelOperationRecordMapper, TravelOperationRecord>
    implements TravelOperationRecordService{

    private  final TravelOperationRecordMapper  travelOperationRecordMapper;

    /**
     * 查询审核中的旅游计划
     */




}




