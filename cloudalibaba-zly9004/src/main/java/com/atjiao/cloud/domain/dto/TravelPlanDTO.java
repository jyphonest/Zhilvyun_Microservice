package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/15 21:50
 * @description: TODO
 **/
@Data
public class TravelPlanDTO extends BaseEntity {

    /**
     * id
     */
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
     * 最高预算
     */
    private Double maxBudget;

    /**
     * 最低预算
     */
    private Double minBudget;

    /**
     * 综合评分
     */
    private Double rating;

    /**
     * 计划开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
     * 点赞
     */
    private Integer likeCount;

    /**
     * 收藏
     */
    private Integer collection;

    /**
     * 旅游计划状态
     */
    private String travelStatus;

    /**
     * 头图
     */
    private String headimg;

    /**
     * 行程具体安排
     */
    private List<TravelPlanDailyDTO> travelPlanDailys;
}
