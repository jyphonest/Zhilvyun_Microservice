package com.atjiao.cloud.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 商品信息表
 * @TableName goods_info
 */
@Data
public class GoodsInfo extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分类id
     */
    private String categoryType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品库存
     */
    private Long goodsStock;

    /**
     * 商品描述
     */
    private String goodsDescription;

    /**
     * 商品状态（1上架 0下架）
     */
    private Integer goodsStatus;

    private Integer goodsType;


}