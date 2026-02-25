package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.UserSearchHistoryRecord;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.UserSearchHistoryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 焦叶鹏
 * * @data 2025/8/31 19:20
 * @description: 智旅地图页面搜索历史记录控制器
 **/
@RestController
@RequestMapping("/travelMap")
@RequiredArgsConstructor
public class TravelMapController {

    private final UserSearchHistoryRecordService userSearchHistoryRecordService;

    /**
     * 记录用户在智旅地图页面的搜索历史
     */
    @RequestMapping("/recordSearchHistory")
    public ResultData<UserSearchHistoryRecord> recordSearchHistory(@RequestBody UserSearchHistoryRecord record) {
        return userSearchHistoryRecordService.recordSearchHistory(record.getSearchContent());
    }
}
