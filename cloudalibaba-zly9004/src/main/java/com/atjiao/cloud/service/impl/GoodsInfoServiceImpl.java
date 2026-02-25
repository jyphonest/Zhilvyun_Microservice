package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.Enum.CategoryTypeEnum;
import com.atjiao.cloud.domain.GoodsCategory;
import com.atjiao.cloud.domain.GoodsOperationRecord;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.mapper.GoodsCategoryMapper;
import com.atjiao.cloud.service.GoodsCategoryService;
import com.atjiao.cloud.service.GoodsOperationRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.GoodsInfo;
import com.atjiao.cloud.service.GoodsInfoService;
import com.atjiao.cloud.mapper.GoodsInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.atjiao.cloud.domain.dto.GoodsInfoDTO;
import com.atjiao.cloud.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.springframework.transaction.annotation.Transactional;
import com.atjiao.cloud.domain.vo.GoodsInfoVo;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 焦叶鹏
* @description 针对表【goods_info(商品信息表)】的数据库操作Service实现
* @createDate 2025-07-19 20:21:44
*/
@Service
@RequiredArgsConstructor
public class GoodsInfoServiceImpl extends ServiceImpl<GoodsInfoMapper, GoodsInfo>
    implements GoodsInfoService{

    private final GoodsCategoryMapper goodsCategoryMapper;
    private final GoodsCategoryService goodsCategoryService;
    private final GoodsOperationRecordService goodsOperationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateGoodsInfo(GoodsInfoDTO dto) {
        Long userId = LoginHelper.getUserId();
        boolean operation;
        if(dto == null){
            throw new IllegalArgumentException("商品信息不能为空");
        }
        /**
         * 正常来说还缺少一步，后期需要判断用户身份是否为合法身份，需要实名认证，这一步后期完成
         */
        GoodsInfo entity = BeanUtil.copyProperties(dto, GoodsInfo.class);
        entity.setCategoryType(CategoryTypeEnum.getNameByCode(dto.getCategoryCode()));
        //新增商品 商品默认为审核中
        entity.setGoodsStatus(2);
        operation=this.saveOrUpdate(entity);
        // GoodsCategory category = new GoodsCategory();
        // category.setGoodsId(entity.getId());
        // category.setName(CategoryTypeEnum.getNameByCode(dto.getCategoryCode()));
        //
        // boolean categoryOperation = goodsCategoryService.saveOrUpdate(category);
        GoodsOperationRecord record = new GoodsOperationRecord();
        record.setGoodsId(entity.getId());
        record.setUsreId(userId);
        record.setOperationType(3);
        record.setOperationInfo("新增商品信息");
        boolean operationRecord = goodsOperationRecordService.save(record);
        return operation&&operationRecord;
    }

    @Override
    public IPage<GoodsInfoVo> queryGoodsInfoList(GoodsInfoDTO dto, PageQuery pageQuery) {
        LambdaQueryWrapper<GoodsInfo> wrapper = new LambdaQueryWrapper<>();
        if (dto.getId() != null) wrapper.eq(GoodsInfo::getId, dto.getId());
        // if (dto.getCategoryId() != null) wrapper.eq(GoodsInfo::getCategoryType, dto.getCategoryId());
        if (dto.getGoodsName() != null) wrapper.like(GoodsInfo::getGoodsName, dto.getGoodsName());
        if (dto.getGoodsStatus() != null) wrapper.eq(GoodsInfo::getGoodsStatus, dto.getGoodsStatus());
        if(dto.getGoodsType()!=null) wrapper.eq(GoodsInfo::getGoodsType,dto.getGoodsType());
        if(dto.getMinPrice()!=null&&dto.getMaxPrice()!=null){
            wrapper.between(GoodsInfo::getGoodsPrice,dto.getMinPrice(),dto.getMaxPrice());
        }
        Page<GoodsInfo> page = new Page<>(
            pageQuery.getPageNum() == null ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum(),
            pageQuery.getPageSize() == null ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize()
        );
        if (pageQuery.getOrderByColumn() != null && pageQuery.getIsAsc() != null) {
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getIsAsc());
            page.addOrder(new OrderItem(pageQuery.getOrderByColumn(), isAsc));
        }
        IPage<GoodsInfo> goodsPage = this.page(page, wrapper);
        List<GoodsInfoVo> voList = goodsPage.getRecords().stream().map(goods -> {
            GoodsInfoVo vo = BeanUtil.copyProperties(goods, GoodsInfoVo.class);
            // 查询分类名称
            LambdaQueryWrapper<GoodsCategory> catWrapper = new LambdaQueryWrapper<>();
            catWrapper.eq(GoodsCategory::getGoodsId, goods.getId());
            GoodsCategory category = goodsCategoryService.getOne(catWrapper, false);
            if (category != null) {
                vo.setCategoryName(category.getName());
                vo.setCategoryId(category.getId());
            }
            return vo;
        }).collect(Collectors.toList());
        Page<GoodsInfoVo> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(goodsPage.getTotal());
        voPage.setSize(goodsPage.getSize());
        voPage.setCurrent(goodsPage.getCurrent());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGoodsInfo(Long id) {
        // 先删除分类表中对应商品的分类
        LambdaQueryWrapper<GoodsCategory> catWrapper = new LambdaQueryWrapper<>();
        catWrapper.eq(GoodsCategory::getGoodsId, id);
        goodsCategoryService.remove(catWrapper);
        // 再删除商品
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditGoodsStatus(GoodsInfoDTO dto) {
        Long userId = LoginHelper.getUserId();
        GoodsInfo goodsInfo = new GoodsInfo();
        if(dto.getId()!=null){
            goodsInfo.setId(dto.getId());
        }
        if(dto.getGoodsStatus()!=null){
            goodsInfo.setGoodsStatus(dto.getGoodsStatus());
        }
        this.saveOrUpdate(goodsInfo);
        GoodsOperationRecord record = new GoodsOperationRecord();
        record.setGoodsId(dto.getId());
        record.setUsreId(userId);
        record.setOperationType(dto.getOperationType());
        record.setNote(dto.getNote());
        boolean goodsStatus =  goodsOperationRecordService.save(record);
        return goodsStatus;
    }

    @Override
    public IPage<GoodsInfoVo> queryMyGoodsList(GoodsInfoDTO dto, PageQuery pageQuery) {

        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<GoodsInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsInfo::getCreateBy, userId);
        if(dto.getGoodsName()!=null&&!dto.getGoodsName().isEmpty()){
            wrapper.eq(GoodsInfo::getGoodsName, dto.getGoodsName());
        }
        Page<GoodsInfo> page = new Page<>(
            pageQuery.getPageNum() == null ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum(),
            pageQuery.getPageSize() == null ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize()
        );

        IPage<GoodsInfo> goodsPage = this.page(page, wrapper);
        List<GoodsInfoVo> voList = goodsPage.getRecords().stream().map(goods -> {
            GoodsInfoVo vo = BeanUtil.copyProperties(goods, GoodsInfoVo.class);
            // 查询分类名称
            LambdaQueryWrapper<GoodsCategory> catWrapper = new LambdaQueryWrapper<>();
            catWrapper.eq(GoodsCategory::getGoodsId, goods.getId());
            GoodsCategory category = goodsCategoryService.getOne(catWrapper, false);
            if (category != null) {
                vo.setCategoryName(category.getName());
                vo.setCategoryId(category.getId());
            }
            return vo;
        }).collect(Collectors.toList());
        Page<GoodsInfoVo> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(goodsPage.getTotal());
        voPage.setSize(goodsPage.getSize());
        voPage.setCurrent(goodsPage.getCurrent());
        return voPage;
    }
}




