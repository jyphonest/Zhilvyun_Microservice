package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/7/24 09:53
 * @description: TODO
 **/
@Data
public class TravelStaytimeRecordDTO extends BaseEntity {

    /**
     * id
     */
    private Long id;

    /**
     * 旅游计划表id
     */
    private Long planId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 停留时长
     */
    private Long stayTime;
}
