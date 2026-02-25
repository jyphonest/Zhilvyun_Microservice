package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.vo.TravelRankListVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.impl.TravelStaytimeRecordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atjiao.cloud.domain.dto.TravelStaytimeRecordDTO;
import com.atjiao.cloud.domain.TravelStaytimeRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/24 09:50
 * @description: 排行榜计算属性实现类
 **/
@RestController
@RequestMapping("/travelStayTimeRecord")
@RequiredArgsConstructor
public class TravelStayTimeRecordController {

    private final TravelStaytimeRecordServiceImpl travelStaytimeRecordService;

    /**
     * 排行榜接口
     * @param type 榜单类型
     * @return List<TravelRankListVo>
     */
    @GetMapping("/rankList")
    public ResultData<List<TravelRankListVo>> rankList(@RequestParam String type) {
        return ResultData.success(travelStaytimeRecordService.rankList(type));
    }

    /**
     * 保存停留时长
     */
    @PostMapping("/save")
    public ResultData<Boolean> saveStayTime(@RequestBody TravelStaytimeRecordDTO dto) {
        TravelStaytimeRecord record = new TravelStaytimeRecord();
        record.setPlanId(dto.getPlanId());
        record.setUserId(dto.getUserId());
        record.setStayTime(dto.getStayTime());
        return ResultData.success(travelStaytimeRecordService.save(record));
    }
}
