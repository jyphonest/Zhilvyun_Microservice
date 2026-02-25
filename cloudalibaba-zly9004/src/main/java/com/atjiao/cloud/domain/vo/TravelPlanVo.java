package com.atjiao.cloud.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/20 20:01
 * @description: TODO
 **/
@Data
public class TravelPlanVo {
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 标题
     */
    private String planTitle;

    /**
     * 出发地
     */
    private String departure;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 预算
     */
    private Double budget;

    /**
     * 综合评分
     */
    private Double rating;

    /**
     * 计划开始日期
     */
    private Date startTime;

    /**
     * 计划结束时间
     */
    private Date endTime;

    /**
     * 总结
     */
    private String summarize;

    /**
     * 温馨提示
     */
    private String kindTips;

    /**
     * 旅游计划状态
     */
    private String travelStatus;

    /**
     * 点赞
     */
    private Integer likeCount;

    /**
     * 收藏
     */
    private Integer collection;

    /**
     * 浏览量
     */
    private Integer pageViews;

    /**
     * 头图
     */
    private String headimg;

    /**
     * 旅游计划oss文件
     */
    private List<SysOssVo> sysOssVos;

    private List<TravelPlanDailyVo> travelPlanDailyVos;

    // 排行榜需要
    private Date createTime;
    private Date updateTime;
}
