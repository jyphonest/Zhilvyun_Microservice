package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.UserSearchHistoryRecord;
import com.atjiao.cloud.resp.ResultData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 焦叶鹏
* @description 针对表【user_search_history_record】的数据库操作Service
* @createDate 2025-08-31 19:18:53
*/
public interface UserSearchHistoryRecordService extends IService<UserSearchHistoryRecord> {

    /**
     * 记录用户在智旅地图页面的搜索历史
     */
    public ResultData<UserSearchHistoryRecord> recordSearchHistory(String searchContent);
}
