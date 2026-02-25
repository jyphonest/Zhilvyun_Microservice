package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/7/19 20:25
 * @description: TODO
 **/
@Data
public class GoodsCategoryDTO extends BaseEntity {
    /**
     * 主键分类id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 分类层级
     */
    private Integer level;
}
