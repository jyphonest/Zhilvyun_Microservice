package com.atjiao.cloud.domain;

import java.util.Date;
import java.util.List;

import com.atjiao.cloud.domain.vo.SysOssVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 旅游计划基本信息表
 * @TableName travel_plan
 */
@Data
@TableName(value ="travel_plan")
public class TravelPlan extends BaseEntity{
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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

}