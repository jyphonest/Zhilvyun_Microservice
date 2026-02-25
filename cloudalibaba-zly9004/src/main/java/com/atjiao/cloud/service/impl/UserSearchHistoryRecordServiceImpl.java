package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.resp.ResultData;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.UserSearchHistoryRecord;
import com.atjiao.cloud.service.UserSearchHistoryRecordService;
import com.atjiao.cloud.mapper.UserSearchHistoryRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 焦叶鹏
* @description 针对表【user_search_history_record】的数据库操作Service实现
* @createDate 2025-08-31 19:18:53
*/
@Service
@RequiredArgsConstructor
public class UserSearchHistoryRecordServiceImpl extends ServiceImpl<UserSearchHistoryRecordMapper, UserSearchHistoryRecord>
    implements UserSearchHistoryRecordService{

    private final UserSearchHistoryRecordMapper userSearchHistoryRecordMapper;
    /**
     * 记录用户在智旅地图页面的搜索历史
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultData<UserSearchHistoryRecord> recordSearchHistory(String searchContent) {
        Long userId = LoginHelper.getUserId();
        UserSearchHistoryRecord record = new UserSearchHistoryRecord();
        record.setUserId(userId);
        record.setSearchContent(searchContent);
        save(record);
        return ResultData.success(record);
    }

}




