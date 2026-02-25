package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.PageQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.SysDictData;
import com.atjiao.cloud.service.SysDictDataService;
import com.atjiao.cloud.mapper.SysDictDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
* @author 86182
* @description 针对表【sys_dict_data】的数据库操作Service实现
* @createDate 2025-11-12 23:09:44
*/
@Slf4j
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
    implements SysDictDataService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDict(SysDictData sysDictData) {
        // 1. 默认状态设置
        if (sysDictData.getStatus() == null) {
            sysDictData.setStatus(0);
        }

        // 2. 校验重复：label 和 value 任意一个重复都不允许
        if (StringUtils.hasText(sysDictData.getDictType())) {

            // 检查 label 是否重复
            if (StringUtils.hasText(sysDictData.getDictLabel())) {
                Long countLabel = this.lambdaQuery()
                        .eq(SysDictData::getDictLabel, sysDictData.getDictLabel())
                        .count();

                if (countLabel != null && countLabel > 0) {
                    throw new RuntimeException("字典标签（label）已存在，请勿重复！");
                }
            }

            // 检查 value 是否重复
            if (StringUtils.hasText(sysDictData.getDictValue())) {
                Long countValue = this.lambdaQuery()
                        .eq(SysDictData::getDictValue, sysDictData.getDictValue())
                        .count();

                if (countValue != null && countValue > 0) {
                    throw new RuntimeException("字典键值（value）已存在，请勿重复！");
                }
            }
        }

        // 3. dict_sort 自动递增逻辑
        if (sysDictData.getDictSort() == null && StringUtils.hasText(sysDictData.getDictType())) {
            LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictData::getDictType, sysDictData.getDictType())
                    .orderByDesc(SysDictData::getDictSort)
                    .last("LIMIT 1");

            SysDictData maxSortDict = this.getOne(wrapper, false);

            if (maxSortDict != null && maxSortDict.getDictSort() != null) {
                sysDictData.setDictSort(maxSortDict.getDictSort() + 1);
            } else {
                sysDictData.setDictSort(1);
            }
        }

        return this.save(sysDictData);
    }


    @Override
    public boolean updateDict(SysDictData sysDictData) {
        if (sysDictData.getDictValue() == null) {
            throw new IllegalArgumentException("字典键值不能为空");
        }
        return this.updateById(sysDictData);
    }

    @Override
    public boolean deleteDict(Long dictCode) {
        if (dictCode == null) {
            throw new IllegalArgumentException("字典编码不能为空");
        }
        // MyBatis-Plus的removeById会自动调用逻辑删除
        return this.removeById(dictCode);
    }

    @Override
    public SysDictData getDictById(Long dictCode) {
        if (dictCode == null) {
            throw new IllegalArgumentException("字典编码不能为空");
        }
        return this.getById(dictCode);
    }

    @Override
    public IPage<SysDictData> queryDictList(SysDictData sysDictData, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (sysDictData != null) {
            if (StringUtils.hasText(sysDictData.getDictType())) {
                wrapper.eq(SysDictData::getDictType, sysDictData.getDictType());
            }
            if (StringUtils.hasText(sysDictData.getDictLabel())) {
                wrapper.like(SysDictData::getDictLabel, sysDictData.getDictLabel());
            }
            if (StringUtils.hasText(sysDictData.getDictValue())) {
                wrapper.like(SysDictData::getDictValue, sysDictData.getDictValue());
            }
            if (sysDictData.getStatus() != null) {
                wrapper.eq(SysDictData::getStatus, sysDictData.getStatus());
            }
        }
        
        // 按dict_sort排序
        wrapper.orderByAsc(SysDictData::getDictSort);
        
        // 分页参数
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) 
                ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();
        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) 
                ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        
        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public List<SysDictData> queryDictByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            throw new IllegalArgumentException("字典类型不能为空");
        }
        
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getStatus, 0)  // 只查询正常状态的
               .orderByAsc(SysDictData::getDictSort);
        
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importDictFromEnum(Class<? extends Enum<?>> enumClass, String dictType) {
        if (enumClass == null) {
            throw new IllegalArgumentException("枚举类不能为空");
        }
        if (!StringUtils.hasText(dictType)) {
            throw new IllegalArgumentException("字典类型不能为空");
        }

        try {
            // 获取枚举类的所有常量
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            if (enumConstants == null || enumConstants.length == 0) {
                throw new IllegalArgumentException("枚举类没有定义任何常量");
            }

            // 查询当前类型下最大的dict_sort值
            LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictData::getDictType, dictType)
                   .orderByDesc(SysDictData::getDictSort)
                   .last("LIMIT 1");
            SysDictData maxSortDict = this.getOne(wrapper, false);
            
            // 计算起始排序值
            int currentSort = (maxSortDict != null && maxSortDict.getDictSort() != null) 
                    ? maxSortDict.getDictSort() + 1 : 1;

            // 通过反射获取getLabel和getCode方法
            Method getLabelMethod = null;
            Method getCodeMethod = null;
            
            // 尝试获取getLabel或getName方法
            try {
                getLabelMethod = enumClass.getMethod("getLabel");
            } catch (NoSuchMethodException e) {
                try {
                    getLabelMethod = enumClass.getMethod("getName");
                } catch (NoSuchMethodException ex) {
                    throw new IllegalArgumentException("枚举类必须包含getLabel()或getName()方法");
                }
            }
            
            // 尝试获取getCode方法
            try {
                getCodeMethod = enumClass.getMethod("getCode");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("枚举类必须包含getCode()方法");
            }

            int successCount = 0;
            
            // 遍历枚举常量，插入数据库
            for (Enum<?> enumConstant : enumConstants) {
                try {
                    // 获取label和code
                    String label = (String) getLabelMethod.invoke(enumConstant);
                    Object codeObj = getCodeMethod.invoke(enumConstant);
                    String code = String.valueOf(codeObj);

                    // 创建字典数据
                    SysDictData dictData = new SysDictData();
                    dictData.setDictType(dictType);
                    dictData.setDictLabel(label);
                    dictData.setDictValue(code);
                    dictData.setDictSort(currentSort++);
                    dictData.setStatus(0);  // 默认正常状态
                    dictData.setRemark("从枚举类 " + enumClass.getSimpleName() + " 导入");

                    // 保存到数据库
                    if (this.save(dictData)) {
                        successCount++;
                    }
                    
                } catch (Exception e) {
                    log.error("导入枚举常量 {} 失败", enumConstant.name(), e);
                    // 继续处理下一个
                }
            }

            log.info("从枚举类 {} 导入字典数据完成，总数: {}，成功: {}", 
                    enumClass.getSimpleName(), enumConstants.length, successCount);
            
            return successCount;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("从枚举类导入字典数据失败", e);
            throw new RuntimeException("导入失败: " + e.getMessage(), e);
        }
    }
}




