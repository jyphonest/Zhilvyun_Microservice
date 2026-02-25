package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 商品分类表
 * @TableName goods_category
 */
@Data
public class GoodsCategory extends BaseEntity {
    /**
     * 主键分类id
     */
    @TableId(type = IdType.ASSIGN_ID)
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