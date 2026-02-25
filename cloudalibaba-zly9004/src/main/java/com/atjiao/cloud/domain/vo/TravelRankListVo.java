package com.atjiao.cloud.domain.vo;

import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/7/24 10:01
 * @description: 排行榜Vo
 **/
@Data
public class TravelRankListVo extends TravelPlanVo{

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 综合得分
     */
    private Double totalScore;

    /**
     * 平均停留时长
     */
    private Long stayTime;


}
