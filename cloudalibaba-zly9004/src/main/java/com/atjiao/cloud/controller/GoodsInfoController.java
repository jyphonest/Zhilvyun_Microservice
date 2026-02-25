package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.vo.GoodsInfoVo;
import com.atjiao.cloud.service.GoodsInfoService;
import lombok.RequiredArgsConstructor;
import com.atjiao.cloud.domain.dto.GoodsInfoDTO;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.GoodsInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.atjiao.cloud.resp.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * @author 焦叶鹏
 * * @data 2025/7/19 20:26
 * @description: 商品审核管理控制层
 **/
@RestController
@RequestMapping("/goodsInfo")
@RequiredArgsConstructor
public class GoodsInfoController {
    private final GoodsInfoService goodsInfoService;

    /**
     * 新增或更新商品
     */
    @PostMapping("/saveOrUpdate")
    public ResultData<Boolean> saveOrUpdate(@RequestBody GoodsInfoDTO dto) {
        return ResultData.success(goodsInfoService.saveOrUpdateGoodsInfo(dto));
    }

    /**
     * 条件+分页查询商品
     */
    @PostMapping("/list")
    public ResultData<IPage<GoodsInfoVo>> list(@RequestBody GoodsInfoDTO dto, PageQuery pageQuery) {
        return ResultData.success(goodsInfoService.queryGoodsInfoList(dto, pageQuery));
    }

    /**
     * 删除商品
     */
    @PostMapping("/delete/{id}")
    public ResultData<Boolean> delete(@PathVariable Long id) {
        return ResultData.success(goodsInfoService.deleteGoodsInfo(id));
    }

    /**
     * 后台页面商品审核或上架和下架商品
     */
    @PostMapping("/goodsAudit")
    public ResultData<Boolean> audit(@RequestBody GoodsInfoDTO dto) {
        return ResultData.success(goodsInfoService.auditGoodsStatus(dto));
    }

    /**
     * 商品兑换接口
     */

    /**
     * 云商城现时活动商品抢购接口
     */

    /**
     * 查询自己上架/的商品
     */
    @PostMapping("/listMyGoods")
    public ResultData<IPage<GoodsInfoVo>> listMyGoods(@RequestBody GoodsInfoDTO dto, PageQuery pageQuery) {
        return ResultData.success(goodsInfoService.queryMyGoodsList(dto, pageQuery));
    }
}
