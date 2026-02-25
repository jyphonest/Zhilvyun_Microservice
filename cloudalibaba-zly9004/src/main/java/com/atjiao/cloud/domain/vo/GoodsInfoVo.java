package com.atjiao.cloud.domain.vo;

import com.atjiao.cloud.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 焦叶鹏
 * * @data 2025/7/20 16:05
 * @description: TODO
 **/
@Data
public class GoodsInfoVo  extends BaseEntity {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 分类id
     */
    private Long categoryId;

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

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品类型
     */
    private Integer goodsType;
}
