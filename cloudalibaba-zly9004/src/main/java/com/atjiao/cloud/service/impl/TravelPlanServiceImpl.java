package com.atjiao.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.atjiao.cloud.Enum.OperationType;
import com.atjiao.cloud.domain.*;
import com.atjiao.cloud.domain.dto.TravelPlanDTO;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.service.TravelPlanService;
import com.atjiao.cloud.service.TravelPlanDailyService;
import com.atjiao.cloud.domain.dto.TravelPlanDailyDTO;
import com.atjiao.cloud.domain.vo.TravelPlanDailyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.atjiao.cloud.domain.vo.TravelPlanVo;
import com.atjiao.cloud.domain.vo.SysOssVo;

import java.util.stream.Collectors;

/**
* @author 焦叶鹏
* @description 针对表【travel_plan(旅游计划基本信息表)】的数据库操作Service实现
* @createDate 2025-07-15 21:27:50
*/
@Service
@RequiredArgsConstructor
public class TravelPlanServiceImpl extends ServiceImpl<TravelPlanMapper, TravelPlan>
    implements TravelPlanService{

    private final TravelPlanMapper travelPlanMapper;
    private final SysOssMapper sysOssMapper;
    private final TravelUserLikeMapper travelUserLikeMapper;
    private final TravelUserFavoriteMapper travelUserFavoriteMapper;
    private final TravelOperationRecordMapper travelOperationRecordMapper;
    private final TravelPlanDailyService travelPlanDailyService;

    /**
     * 1->查询旅游计划表详细信息（含OSS文件信息）
     * @param id
     * @return
     */
    @Override
    public TravelPlanVo getOneById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("旅游计划表id不能为空");
        }
        TravelPlan plan = travelPlanMapper.selectById(id);
        if (plan == null) return null;
        TravelPlanVo vo = BeanUtil.copyProperties(plan, TravelPlanVo.class);
        // 查询每日详情
        LambdaQueryWrapper<TravelPlanDaily> dailyQuery = new LambdaQueryWrapper<>();
        dailyQuery.eq(TravelPlanDaily::getPlanId, id);
        List<TravelPlanDaily> dailyList = travelPlanDailyService.list(dailyQuery);
        List<TravelPlanDailyVo> dailyVoList = dailyList.stream().map(daily -> {
            TravelPlanDailyVo dailyVo = BeanUtil.copyProperties(daily, TravelPlanDailyVo.class);
            // 查询OSS文件（biz_id为daily的id）
            LambdaQueryWrapper<SysOss> ossQuery = new LambdaQueryWrapper<>();
            ossQuery.eq(SysOss::getBizId, daily.getId()).eq(SysOss::getDelFlag, 0);
            List<SysOss> ossList = sysOssMapper.selectList(ossQuery);
            List<SysOssVo> ossVoList = ossList.stream().map(oss -> {
                SysOssVo ossVo = new SysOssVo();
                ossVo.setOssId(oss.getOssId());
                ossVo.setFileName(oss.getFileName());
                ossVo.setUrl(oss.getUrl());
                ossVo.setBizType(oss.getBizType());
                return ossVo;
            }).collect(Collectors.toList());
            dailyVo.setSysOssVos(ossVoList); // 若TravelPlanDailyVo有此字段可放开
            return dailyVo;
        }).collect(Collectors.toList());
        vo.setTravelPlanDailyVos(dailyVoList);
        // 查询主表OSS文件（biz_id为plan id）
        LambdaQueryWrapper<SysOss> ossQuery = new LambdaQueryWrapper<>();
        ossQuery.eq(SysOss::getBizId, id).eq(SysOss::getDelFlag, 0);
        List<SysOss> ossList = sysOssMapper.selectList(ossQuery);
        List<SysOssVo> ossVoList = ossList.stream().map(oss -> {
            SysOssVo ossVo = new SysOssVo();
            ossVo.setOssId(oss.getOssId());
            ossVo.setFileName(oss.getFileName());
            ossVo.setUrl(oss.getUrl());
            ossVo.setBizType(oss.getBizType());
            return ossVo;
        }).collect(Collectors.toList());
        vo.setSysOssVos(ossVoList);
        return vo;
    }

    /**
     * 2->查询旅游计划表列表信息（含OSS文件信息）
     * @param travelPlanDTO
     * @return
     */
    @Override
    public IPage<TravelPlanVo> queryTravelPlanList(TravelPlanDTO travelPlanDTO,PageQuery pageQuery) {
        if (travelPlanDTO == null) {
            throw new IllegalArgumentException("旅游计划表查询条件不能为空");
        }
        //构建查询条件
        LambdaQueryWrapper<TravelPlan> wrapper = new LambdaQueryWrapper<>();
        if(travelPlanDTO.getId()!=null){
            wrapper.eq(TravelPlan::getId,travelPlanDTO.getId());
        }
        if(travelPlanDTO.getTravelStatus()!=null&&!travelPlanDTO.getTravelStatus().isEmpty()){
            wrapper.eq(TravelPlan::getTravelStatus,travelPlanDTO.getTravelStatus());
        }
        if(travelPlanDTO.getPlanTitle()!=null&&!travelPlanDTO.getPlanTitle().isEmpty()){
            wrapper.like(TravelPlan::getPlanTitle,travelPlanDTO.getPlanTitle());
        }
        if(travelPlanDTO.getDeparture()!=null&&!travelPlanDTO.getDeparture().isEmpty()){
            wrapper.eq(TravelPlan::getDeparture,travelPlanDTO.getDeparture());
        }
        if(travelPlanDTO.getDestination()!=null&&!travelPlanDTO.getDestination().isEmpty()){
            wrapper.eq(TravelPlan::getDestination,travelPlanDTO.getDestination());
        }
        if(travelPlanDTO.getMaxBudget()!=null&&travelPlanDTO.getMaxBudget()>0){
            wrapper.le(TravelPlan::getBudget,travelPlanDTO.getMaxBudget());
        }
        if(travelPlanDTO.getMinBudget()!=null&&travelPlanDTO.getMinBudget()>0){
            wrapper.ge(TravelPlan::getBudget,travelPlanDTO.getMinBudget());
        }
        if(travelPlanDTO.getUserId()!=null&&travelPlanDTO.getUserId()>0){
            wrapper.eq(TravelPlan::getUserId,travelPlanDTO.getUserId());
        }
        if(travelPlanDTO.getStartTime()!=null){
            wrapper.ge(TravelPlan::getCreateTime,travelPlanDTO.getStartTime());
        }
        if(travelPlanDTO.getEndTime()!=null){
            wrapper.le(TravelPlan::getCreateTime,travelPlanDTO.getEndTime());
        }

        // 添加分页支持
        // 设置默认值
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();
        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        
        // 创建分页对象
        IPage<TravelPlan> page = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        IPage<TravelPlan> travelPlanPage = travelPlanMapper.selectPage(page, wrapper);
        
        // 转换为VO并封装分页结果
        return convertTravelPlanPageToVoPage(travelPlanPage);
    }

    /**
     * 将TravelPlan分页数据转换为TravelPlanVo分页数据
     * @param travelPlanPage
     * @return
     */
    private IPage<TravelPlanVo> convertTravelPlanPageToVoPage(IPage<TravelPlan> travelPlanPage) {
        List<TravelPlan> planList = travelPlanPage.getRecords();
        
        List<TravelPlanVo> voList = planList.stream().map(plan -> {
            TravelPlanVo vo = BeanUtil.copyProperties(plan, TravelPlanVo.class);
            // 查询每日详情
            LambdaQueryWrapper<TravelPlanDaily> dailyQuery = new LambdaQueryWrapper<>();
            dailyQuery.eq(TravelPlanDaily::getPlanId, plan.getId());
            List<TravelPlanDaily> dailyList = travelPlanDailyService.list(dailyQuery);
            List<TravelPlanDailyVo> dailyVoList = dailyList.stream().map(daily -> {
                TravelPlanDailyVo dailyVo = BeanUtil.copyProperties(daily, TravelPlanDailyVo.class);
                // 查询OSS文件（biz_id为daily的id）
                LambdaQueryWrapper<SysOss> ossQuery = new LambdaQueryWrapper<>();
                ossQuery.eq(SysOss::getBizId, daily.getId()).eq(SysOss::getDelFlag, 0);
                List<SysOss> ossList = sysOssMapper.selectList(ossQuery);
                List<SysOssVo> ossVoList = ossList.stream().map(oss -> {
                    SysOssVo ossVo = new SysOssVo();
                    ossVo.setOssId(oss.getOssId());
                    ossVo.setFileName(oss.getFileName());
                    ossVo.setUrl(oss.getUrl());
                    ossVo.setBizType(oss.getBizType());
                    return ossVo;
                }).collect(Collectors.toList());
                dailyVo.setSysOssVos(ossVoList); // 若TravelPlanDailyVo有此字段可放开
                return dailyVo;
            }).collect(Collectors.toList());
            vo.setTravelPlanDailyVos(dailyVoList);
            // 查询主表OSS文件（biz_id为plan id）
            LambdaQueryWrapper<SysOss> ossQuery = new LambdaQueryWrapper<>();
            ossQuery.eq(SysOss::getBizId, plan.getId()).eq(SysOss::getDelFlag, 0);
            List<SysOss> ossList = sysOssMapper.selectList(ossQuery);
            List<SysOssVo> ossVoList = ossList.stream().map(oss -> {
                SysOssVo ossVo = new SysOssVo();
                ossVo.setOssId(oss.getOssId());
                ossVo.setFileName(oss.getFileName());
                ossVo.setUrl(oss.getUrl());
                ossVo.setBizType(oss.getBizType());
                return ossVo;
            }).collect(Collectors.toList());
            vo.setSysOssVos(ossVoList);
            return vo;
        }).collect(Collectors.toList());
        
        // 构造分页结果
        IPage<TravelPlanVo> resultPage = new Page<>(travelPlanPage.getCurrent(), travelPlanPage.getSize(), travelPlanPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    /**
     * 3->保存和更新旅游计划表信息
     * @param travelPlanDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TravelPlanDailyVo> saveTravelPlan(TravelPlanDTO travelPlanDTO) {
        Long userId = LoginHelper.getUserId();
        if (travelPlanDTO == null) {
            throw new IllegalArgumentException("旅游计划表信息不能为空");
        }

        // 保存或更新旅行计划
        TravelPlan travelPlan;
        if (travelPlanDTO.getId() == null) {
            // 新增旅游计划
            travelPlanDTO.setUserId(userId);
            travelPlanDTO.setTravelStatus("3");
            travelPlan = BeanUtil.copyProperties(travelPlanDTO, TravelPlan.class);
            this.save(travelPlan);

            // 记录操作
            TravelOperationRecord travelOperationRecord = new TravelOperationRecord();
            travelOperationRecord.setPlanId(travelPlan.getId());
            travelOperationRecord.setUserid(userId);
            travelOperationRecord.setOperationType(3);
            travelOperationRecord.setOperationInfo("发布旅游计划");
            travelOperationRecordMapper.insert(travelOperationRecord);
        } else {
            // 更新旅游计划
            travelPlan = BeanUtil.copyProperties(travelPlanDTO, TravelPlan.class);
            travelPlan.setPlanTitle(travelPlanDTO.getPlanTitle());
            travelPlan.setDeparture(travelPlanDTO.getDeparture());
            travelPlan.setDestination(travelPlanDTO.getDestination());
            updateById(travelPlan);

            // 删除原有每日详情
            LambdaQueryWrapper<TravelPlanDaily> delQuery = new LambdaQueryWrapper<>();
            delQuery.eq(TravelPlanDaily::getPlanId, travelPlan.getId());
            travelPlanDailyService.remove(delQuery);
        }

        // 处理每日计划并返回 TravelPlanDailyVo 列表
        List<TravelPlanDailyVo> dailyVoList = new ArrayList<>();
        if (CollUtil.isNotEmpty(travelPlanDTO.getTravelPlanDailys())) {
            for (TravelPlanDailyDTO dailyDTO : travelPlanDTO.getTravelPlanDailys()) {
                TravelPlanDaily daily = BeanUtil.copyProperties(dailyDTO, TravelPlanDaily.class);
                daily.setPlanId(travelPlan.getId());
                travelPlanDailyService.save(daily);
                // 转换为 TravelPlanDailyVo
                TravelPlanDailyVo dailyVo = BeanUtil.copyProperties(daily, TravelPlanDailyVo.class);
                dailyVoList.add(dailyVo);
            }
        }

        return dailyVoList;
    }

    /**
     * 4->删除旅游计划表信息
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTravelPlan(Long id) {
        if(id==null){
            throw new IllegalArgumentException("旅游计划表id不能为空");
        }
        // 删除每日详情
        LambdaQueryWrapper<TravelPlanDaily> delQuery = new LambdaQueryWrapper<>();
        delQuery.eq(TravelPlanDaily::getPlanId, id);
        travelPlanDailyService.remove(delQuery);
        // 删除主表
        Boolean result = this.removeById(id);
        return result;
    }

    /**
     * 5->暂存旅游计划表信息
     * @param travelPlanDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveTravelPlanStash(TravelPlanDTO travelPlanDTO) {
        Long userId = LoginHelper.getUserId();
        TravelPlan travelPlan = BeanUtil.copyProperties(travelPlanDTO, TravelPlan.class);
        travelPlan.setUserId(userId);
        travelPlan.setTravelStatus("0");
        this.save(travelPlan);
        return true;
    }

    /**
     * 6->旅游计划表点赞和取消点赞操作
     * @param planId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeTravelPlan(Long planId) {
        Long userId = LoginHelper.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("用户未登录");
        }

        // 查询用户对该计划的最新点赞记录
        LambdaQueryWrapper<TravelUserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelUserLike::getUserId, userId)
               .eq(TravelUserLike::getPlanId, planId)
               .orderByDesc(TravelUserLike::getCreateTime)
               .last("LIMIT 1");
        TravelUserLike latestRecord = travelUserLikeMapper.selectOne(wrapper);

        // 获取当前旅游计划
        TravelPlan travelPlan = travelPlanMapper.selectById(planId);
        if (travelPlan == null) {
            throw new IllegalArgumentException("旅游计划不存在");
        }

        // 创建新的点赞记录
        TravelUserLike newRecord = new TravelUserLike();
        newRecord.setUserId(userId);
        newRecord.setPlanId(planId);

        // 判断操作类型和更新点赞数
        if (latestRecord == null || latestRecord.getOperationType() == 1) {
            // 没有记录或最新记录是取消点赞，执行点赞操作
            newRecord.setOperationType(0);
            travelUserLikeMapper.insert(newRecord);

            // 增加点赞数
            int currentLikeCount = travelPlan.getLikeCount() == null ? 0 : travelPlan.getLikeCount();
            travelPlan.setLikeCount(currentLikeCount + 1);
        } else {
            // 最新记录是点赞，执行取消点赞操作
            newRecord.setOperationType(1);
            travelUserLikeMapper.insert(newRecord);

            // 减少点赞数
            int currentLikeCount = travelPlan.getLikeCount() == null ? 0 : travelPlan.getLikeCount();
            travelPlan.setLikeCount(Math.max(0, currentLikeCount - 1));
        }

        // 更新旅游计划的点赞数
        return travelPlanMapper.updateById(travelPlan) > 0;
    }

    /**
     * 7->旅游计划表收藏和取消收藏操作
     * @param planId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean collectTravelPlan(Long planId) {
        Long userId = LoginHelper.getUserId();
        if (planId == null) {
            throw new IllegalArgumentException("旅游计划id不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户未登录");
        }

        // 查询用户对该计划的最新收藏记录
        LambdaQueryWrapper<TravelUserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelUserFavorite::getUserId, userId)
               .eq(TravelUserFavorite::getPlanId, planId)
               .orderByDesc(TravelUserFavorite::getCreateTime)
               .last("LIMIT 1");
        TravelUserFavorite latestRecord = travelUserFavoriteMapper.selectOne(wrapper);

        // 获取当前旅游计划
        TravelPlan travelPlan = travelPlanMapper.selectById(planId);
        if (travelPlan == null) {
            throw new IllegalArgumentException("旅游计划不存在");
        }

        // 创建新的收藏记录
        TravelUserFavorite newRecord = new TravelUserFavorite();
        newRecord.setUserId(userId);
        newRecord.setPlanId(planId);

        // 判断操作类型和更新收藏数
        if (latestRecord == null || latestRecord.getOperationType() == 1) {
            // 没有记录或最新记录是取消收藏，执行收藏操作
            newRecord.setOperationType(0);
            travelUserFavoriteMapper.insert(newRecord);

            // 增加收藏数
            int currentCollection = travelPlan.getCollection() == null ? 0 : travelPlan.getCollection();
            travelPlan.setCollection(currentCollection + 1);
        } else {
            // 最新记录是收藏，执行取消收藏操作
            newRecord.setOperationType(1);
            travelUserFavoriteMapper.insert(newRecord);

            // 减少收藏数
            int currentCollection = travelPlan.getCollection() == null ? 0 : travelPlan.getCollection();
            travelPlan.setCollection(Math.max(0, currentCollection - 1));
        }

        // 更新旅游计划的收藏数
        return travelPlanMapper.updateById(travelPlan) > 0;
    }


    /**
     * 8->旅游计划表增加浏览量
     *
     */
    @Override
    public Boolean increasePageViews(Long planId) {
        TravelPlan travelPlan = this.getById(planId);
        if (travelPlan == null) {
            throw new IllegalArgumentException("旅游计划不存在");
        }
        int pageViews = travelPlan.getPageViews() == null ? 0 : travelPlan.getPageViews();
        travelPlan.setPageViews(pageViews + 1);
        return this.updateById(travelPlan);
    }

    /**
     * 9->查询自己发布的旅游计划列表
     * @param pageQuery
     * @return
     */
    @Override
    public IPage<TravelPlanVo> queryCollectTravelPlanList(PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        
        // 构建查询条件：查询当前用户发布的且travel_status为1的旅游计划
        LambdaQueryWrapper<TravelPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelPlan::getUserId, userId);
        
        // 添加分页支持
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();
        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        
        // 创建分页对象
        IPage<TravelPlan> page = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        IPage<TravelPlan> travelPlanPage = travelPlanMapper.selectPage(page, wrapper);
        
        // 转换为VO并封装分页结果
        return convertTravelPlanPageToVoPage(travelPlanPage);
    }

    /**
     * 10->审核旅游计划表
     * @param planId
     * @return
     */
    @Override
    public boolean auditTravelPlan(Long planId,Integer operationType,String note) {
        Long userId = LoginHelper.getUserId();
        // 审核逻辑
        TravelPlan travelPlan = new TravelPlan();
        travelPlan = this.getById(planId);
        if(travelPlan == null){
            throw new IllegalArgumentException("旅游计划不存在");
        }
        if(travelPlan.getTravelStatus().equals(1L)|| travelPlan.getTravelStatus().equals(5L)){
            throw new IllegalArgumentException("旅游计划已审核通过，无需重复操作");
        }


        LambdaUpdateWrapper<TravelPlan> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TravelPlan::getId,planId);
        updateWrapper.set(TravelPlan::getTravelStatus,operationType);
        updateWrapper.set(TravelPlan::getUpdateTime,new Date());
        this.update(travelPlan,updateWrapper);

        TravelOperationRecord travelOperationRecord = new TravelOperationRecord();
        travelOperationRecord.setPlanId(planId);
        travelOperationRecord.setUserid(userId);
        travelOperationRecord.setOperationType(operationType);
        travelOperationRecord.setOperationInfo(OperationType.getNameByCode(operationType));
        travelOperationRecord.setNote(note);
        travelOperationRecordMapper.insert(travelOperationRecord);
        return true;
    }

    @Override
    public IPage<TravelPlanVo> queryUserCollectTravelPlanList(TravelPlanDTO travelPlanDTO,PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        
        // 查询用户所有收藏操作，按时间倒序，便于取每个计划的最新一条操作
        LambdaQueryWrapper<TravelUserFavorite> favoriteQuery = new LambdaQueryWrapper<>();
        favoriteQuery.eq(TravelUserFavorite::getUserId, userId)
                     .orderByDesc(TravelUserFavorite::getCreateTime);
        List<TravelUserFavorite> allOps = travelUserFavoriteMapper.selectList(favoriteQuery);

        if (CollectionUtils.isEmpty(allOps)) {
            return new Page<>(pageQuery.getPageNum() == null ? 1 : pageQuery.getPageNum(), 
                             pageQuery.getPageSize() == null ? 10 : pageQuery.getPageSize(), 0);
        }

        // 对同一用户-计划分组：只保留每个planId的最新一条操作
        Map<Long, TravelUserFavorite> latestByPlan = new LinkedHashMap<>();
        for (TravelUserFavorite op : allOps) {
            if (!latestByPlan.containsKey(op.getPlanId())) {
                latestByPlan.put(op.getPlanId(), op);
            }
        }

        // 仅保留最新操作为"收藏(0)"的计划ID
        List<Long> collectedPlanIds = latestByPlan.values().stream()
                .filter(op -> op.getOperationType() != null && op.getOperationType() == 0)
                .map(TravelUserFavorite::getPlanId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collectedPlanIds)) {
            return new Page<>(pageQuery.getPageNum() == null ? 1 : pageQuery.getPageNum(), 
                             pageQuery.getPageSize() == null ? 10 : pageQuery.getPageSize(), 0);
        }

        // 构建旅游计划查询条件
        LambdaQueryWrapper<TravelPlan> planQuery = new LambdaQueryWrapper<>();
        planQuery.in(TravelPlan::getId, collectedPlanIds);
        
        // 如果有标题筛选条件
        if(travelPlanDTO != null && travelPlanDTO.getPlanTitle() != null && !travelPlanDTO.getPlanTitle().isEmpty()) {
            planQuery.like(TravelPlan::getPlanTitle, travelPlanDTO.getPlanTitle());
        }

        // 添加分页支持
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();
        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        
        IPage<TravelPlan> page = new Page<>(pageNum, pageSize);
        IPage<TravelPlan> travelPlanPage = travelPlanMapper.selectPage(page, planQuery);
        
        // 转换为VO并封装分页结果
        return convertTravelPlanPageToVoPage(travelPlanPage);
    }
}




