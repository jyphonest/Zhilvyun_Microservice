package com.atjiao.cloud.util;

import com.atjiao.cloud.domain.TravelPlan;
import java.util.Date;

public class TravelRankUtil {
    // 人气榜分数
    public static double calcPopularityScore(TravelPlan plan, int days) {
        double like = plan.getLikeCount() == null ? 0 : plan.getLikeCount();
        double fav = plan.getCollection() == null ? 0 : plan.getCollection();
        double pv = plan.getPageViews() == null ? 0 : plan.getPageViews();
        if (days <= 0) days = 1;
        return (like * 1 + fav * 1.5 + pv * 0.1) / days;
    }
    // 热门榜分数（需传入近7天的点赞、收藏、浏览量）
    public static double calcHotScore(int like7, int fav7, int pv7) {
        return like7 * 2 + fav7 * 3 + pv7 * 0.5;
    }
    // 飙升榜分数（需传入近3天和前3天互动量）
    public static double calcRisingScore(int recent3, int prev3) {
        if (prev3 == 0) return recent3 * 1.0;
        return (recent3 - prev3) * 1.0 / prev3 * 100;
    }
    // 精选榜分数（需传入停留时长、收藏、浏览量）
    public static double calcSelectedScore(long stayTime, int fav, int pv) {
        return stayTime * 0.3 + fav * 0.5 + pv * 1.0;
    }
    // 宝藏榜分数（需传入收藏、浏览量）
    public static double calcTreasureScore(int fav, int pv) {
        if (pv < 100) return 0;
        return fav * 1.0 / pv * 100;
    }
    // 必玩榜分数（需传入完成率、点赞、收藏、浏览量）
    public static double calcMustPlayScore(double finishRate, int like, int fav, int pv) {
        return finishRate * 5 + (like + fav) * 1.0 + pv * 2;
    }
    // 新热榜分数（需传入7天互动量、发布天数、停留时长）
    public static double calcNewHotScore(int interact7, int days, long stayTime) {
        return interact7 * 0.8 + stayTime * 0.2;
    }
} 