package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 
 * @TableName user_search_history_record
 */
@Data
public class UserSearchHistoryRecord extends  BaseEntity{
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
     * 搜索内容
     */
    private String searchContent;

    /**
     * 类型
     */
    private Integer type;

}