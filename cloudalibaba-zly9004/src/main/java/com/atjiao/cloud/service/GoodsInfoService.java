package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.GoodsInfo;
import com.atjiao.cloud.domain.dto.GoodsInfoDTO;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.vo.GoodsInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 焦叶鹏
* @description 针对表【goods_info(商品信息表)】的数据库操作Service
* @createDate 2025-07-19 20:21:44
*/
public interface GoodsInfoService extends IService<GoodsInfo> {
    /**
     * 新增或更新商品
     */
    boolean saveOrUpdateGoodsInfo(GoodsInfoDTO dto);

    /**
     * 条件+分页查询商品
     */
    IPage<GoodsInfoVo> queryGoodsInfoList(GoodsInfoDTO dto, PageQuery pageQuery);

    /**
     * 删除商品（级联删除分类）
     */
    boolean deleteGoodsInfo(Long id);

    /**
     * 审核商品信息
     */
    boolean auditGoodsStatus(GoodsInfoDTO dto);

    /**
     * 查询自己上架的商品
     */
    IPage<GoodsInfoVo> queryMyGoodsList(GoodsInfoDTO dto, PageQuery pageQuery);
}
