package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.TravelPlan;
import com.atjiao.cloud.domain.TravelUserLike;
import com.atjiao.cloud.domain.TravelUserFavorite;
import com.atjiao.cloud.domain.vo.TravelRankListVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.TravelStaytimeRecord;
import com.atjiao.cloud.service.TravelStaytimeRecordService;
import com.atjiao.cloud.mapper.TravelPlanMapper;
import com.atjiao.cloud.mapper.TravelUserLikeMapper;
import com.atjiao.cloud.mapper.TravelUserFavoriteMapper;
import com.atjiao.cloud.util.TravelRankUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.atjiao.cloud.mapper.TravelStaytimeRecordMapper;

/**
* @author 焦叶鹏
* @description 针对表【travel_staytime_record】的数据库操作Service实现
* @createDate 2025-07-24 09:51:42
*/
@Service
@RequiredArgsConstructor
public class TravelStaytimeRecordServiceImpl extends ServiceImpl<TravelStaytimeRecordMapper, TravelStaytimeRecord>
    implements TravelStaytimeRecordService{

    private final TravelPlanMapper travelPlanMapper;
    private final TravelUserLikeMapper travelUserLikeMapper;
    private final TravelUserFavoriteMapper travelUserFavoriteMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // 榜单类型常量
    public static final String POPULAR = "popularity";
    public static final String HOT = "hot";
    public static final String RISING = "rising";
    public static final String SELECTED = "selected";
    public static final String TREASURE = "treasure";
    public static final String MUSTPLAY = "mustplay";
    public static final String NEWHOT = "newhot";

    /**
     * 计算并缓存排行榜
     * @param type 榜单类型
     * @return List<TravelRankListVo>
     */
    public List<TravelRankListVo> rankList(String type) {
        String redisKey = "rankList:" + type;
        // 优先从Redis获取
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached instanceof List<?>) {
            List<?> list = (List<?>) cached;
            if (!list.isEmpty() && list.get(0) instanceof TravelRankListVo) {
                return (List<TravelRankListVo>) list;
            }
        }
        // 没有缓存则重新计算
        List<TravelPlan> plans = travelPlanMapper.selectList(null);
        List<TravelRankListVo> result = new ArrayList<>();
        Date now = new Date();
        for (TravelPlan plan : plans) {
            TravelRankListVo vo = new TravelRankListVo();
            vo.setId(plan.getId());
            vo.setPlanTitle(plan.getPlanTitle());
            vo.setUserId(plan.getUserId());
            vo.setLikeCount(plan.getLikeCount());
            vo.setCollection(plan.getCollection());
            vo.setPageViews(plan.getPageViews());
            vo.setUpdateTime(plan.getUpdateTime());
            vo.setCreateTime(plan.getCreateTime());
            vo.setStartTime(plan.getStartTime());
            vo.setEndTime(plan.getEndTime());
            vo.setTravelStatus(plan.getTravelStatus());
            vo.setHeadimg(plan.getHeadimg());
            vo.setDeparture(plan.getDeparture());
            vo.setDestination(plan.getDestination());
            vo.setBudget(plan.getBudget());
            vo.setRating(plan.getRating());
            vo.setSummarize(plan.getSummarize());
            vo.setKindTips(plan.getKindTips());
            // 计算天数
            int days = (int)((now.getTime() - plan.getCreateTime().getTime()) / (1000 * 3600 * 24)) + 1;
            // 统计平均停留时长
            long stayTime = 0L;
            Long avgStay = null;
            try {
                avgStay = this.baseMapper.getAvgStayTimeByPlanId(plan.getId());
            } catch (Exception ignore) {}
            if (avgStay != null) stayTime = avgStay;
            vo.setStayTime(stayTime);
            // 统计近7天/3天互动量
            int like7 = 0, fav7 = 0, pv7 = 0, interact7 = 0;
            int like3 = 0, fav3 = 0, pv3 = 0, interact3 = 0;
            int like3Prev = 0, fav3Prev = 0, pv3Prev = 0, interact3Prev = 0;
            // TODO: 统计点赞、收藏、浏览量的时间段数据，可用baseMapper自定义SQL
            // 计算分数
            double score = 0;
            switch (type) {
                case POPULAR:
                    score = TravelRankUtil.calcPopularityScore(plan, days);
                    break;
                case HOT:
                    score = TravelRankUtil.calcHotScore(like7, fav7, pv7);
                    break;
                case RISING:
                    score = TravelRankUtil.calcRisingScore(interact3, interact3Prev);
                    break;
                case SELECTED:
                    score = TravelRankUtil.calcSelectedScore(stayTime, plan.getCollection() == null ? 0 : plan.getCollection(), plan.getPageViews() == null ? 0 : plan.getPageViews());
                    break;
                case TREASURE:
                    score = TravelRankUtil.calcTreasureScore(plan.getCollection() == null ? 0 : plan.getCollection(), plan.getPageViews() == null ? 0 : plan.getPageViews());
                    break;
                case MUSTPLAY:
                    score = TravelRankUtil.calcMustPlayScore(60, plan.getLikeCount() == null ? 0 : plan.getLikeCount(), plan.getCollection() == null ? 0 : plan.getCollection(), plan.getPageViews() == null ? 0 : plan.getPageViews());
                    break;
                case NEWHOT:
                    score = TravelRankUtil.calcNewHotScore(interact7, days, stayTime);
                    break;
                default:
                    break;
            }
            vo.setTotalScore(score);
            result.add(vo);
        }
        // 排序
        result = result.stream().sorted((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore())).collect(Collectors.toList());
        // 设置排名
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }
        // 存入Redis
        redisTemplate.opsForValue().set(redisKey, result);
        return result;
    }
}




