package com.atjiao.cloud.domain.dto;

import com.atjiao.cloud.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 焦叶鹏
 * * @data 2025/7/19 20:22
 * @description: TODO
 **/
@Data
public class GoodsInfoDTO extends BaseEntity{
    /**
     * 主键id
     */
    private Long id;

    /**
     * 分类id
     */
    private Long categoryType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

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
     * 商品状态（1上架 0下架 2待审核 3审核不通过 4审核通过）
     */
    private Integer goodsStatus;

    /**
     * 商品分类
     */
    private Integer categoryCode;

    private Integer operationType;

    private String note;

    private Integer goodsType;
}
