package com.atjiao.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.TravelPlanDaily;
import com.atjiao.cloud.service.TravelPlanDailyService;
import com.atjiao.cloud.mapper.TravelPlanDailyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

/**
* @author 焦叶鹏
* @description 针对表【travel_plan_daily(旅游计划详情表)】的数据库操作Service实现
* @createDate 2025-07-15 21:28:32
*/
@Service
public class TravelPlanDailyServiceImpl extends ServiceImpl<TravelPlanDailyMapper, TravelPlanDaily>
    implements TravelPlanDailyService{

    /**
     * 创建每日旅游计划
     *
     * @param travelPlanDaily 要创建的每日旅游计划对象
     * @return 创建成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTravelPlanDaily(TravelPlanDaily travelPlanDaily) {
        return this.save(travelPlanDaily);
    }

    /**
     * 根据ID获取每日旅行计划
     *
     * @param id 每日旅行计划的ID
     * @return 对应的每日旅行计划对象，如果ID不存在则返回null
     */
    @Override
    public TravelPlanDaily getTravelPlanDailyById(Long id) {
        return this.getById(id);
    }

    /**
     * 根据查询条件列出旅行计划每日详情列表
     *
     * @param query 查询条件，包括计划ID和日期编号等
     * @return 符合查询条件的旅行计划每日详情列表
     */
    @Override
    public java.util.List<TravelPlanDaily> listTravelPlanDaily(TravelPlanDaily query) {
        LambdaQueryWrapper<TravelPlanDaily> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (query.getPlanId() != null) wrapper.eq(TravelPlanDaily::getPlanId, query.getPlanId());
            if (query.getDayNumber() != null) wrapper.eq(TravelPlanDaily::getDayNumber, query.getDayNumber());
            // 可根据需要添加更多条件
        }
        return this.list(wrapper);
    }

    /**
     * 更新每日旅行计划信息
     *
     * @param travelPlanDaily 要更新的每日旅行计划对象
     * @return 更新是否成功，成功返回true，失败返回false
     */
    @Override
    public boolean updateTravelPlanDaily(TravelPlanDaily travelPlanDaily) {
        return this.updateById(travelPlanDaily);
    }

    /**
     * 删除旅行计划日程
     *
     * @param id 日程的ID
     * @return 如果删除成功，则返回true；否则返回false
     */
    @Override
    public boolean deleteTravelPlanDaily(Long id) {
        return this.removeById(id);
    }
}




