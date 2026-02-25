package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.SysDictData;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.SysDictDataService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 焦叶鹏
 * @data 2025/11/12 23:12
 * @description: 字典表控制层
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    /**
     * 新增字典数据
     * @param sysDictData 字典数据
     * @return 是否成功
     */
    @PostMapping("/add")
    public ResultData<Boolean> addDict(@RequestBody SysDictData sysDictData) {
        return ResultData.success(sysDictDataService.saveDict(sysDictData));
    }

    /**
     * 更新字典数据
     * @param sysDictData 字典数据
     * @return 是否成功
     */
    @PostMapping("/update")
    public ResultData<Boolean> updateDict(@RequestBody SysDictData sysDictData) {
        return ResultData.success(sysDictDataService.updateDict(sysDictData));
    }

    /**
     * 删除字典数据
     * @param dictCode 字典编码
     * @return 是否成功
     */
    @PostMapping("/delete/{dictCode}")
    public ResultData<Boolean> deleteDict(@PathVariable Long dictCode) {
        return ResultData.success(sysDictDataService.deleteDict(dictCode));
    }

    /**
     * 根据ID查询字典数据
     * @param dictCode 字典编码
     * @return 字典数据
     */
    @GetMapping("/get/{dictCode}")
    public ResultData<SysDictData> getDictById(@PathVariable Long dictCode) {
        return ResultData.success(sysDictDataService.getDictById(dictCode));
    }

    /**
     * 分页查询字典数据
     * @param sysDictData 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public ResultData<IPage<SysDictData>> queryDictList(
            @RequestBody(required = false) SysDictData sysDictData,
            PageQuery pageQuery) {
        return ResultData.success(sysDictDataService.queryDictList(sysDictData, pageQuery));
    }

    /**
     * 根据字典类型查询字典数据列表
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @GetMapping("/type/{dictType}")
    public ResultData<List<SysDictData>> queryDictByType(@PathVariable String dictType) {
        return ResultData.success(sysDictDataService.queryDictByType(dictType));
    }

    /**
     * 从枚举类导入字典数据
     * @param enumClassName 枚举类全限定名（如：com.atjiao.cloud.Enum.GoodsTypeEnum）
     * @param dictType 字典类型
     * @return 导入成功的记录数
     */
    @PostMapping("/import")
    public ResultData<Integer> importDictFromEnum(
            @RequestParam String enumClassName,
            @RequestParam String dictType) {
        try {
            // 加载枚举类
            Class<?> clazz = Class.forName(enumClassName);
            if (!clazz.isEnum()) {
                return ResultData.fail("400", "指定的类不是枚举类：" + enumClassName);
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
            
            int count = sysDictDataService.importDictFromEnum(enumClass, dictType);
            return ResultData.success(count);
        } catch (ClassNotFoundException e) {
            return ResultData.fail("404", "找不到指定的枚举类：" + enumClassName);
        } catch (Exception e) {
            return ResultData.fail("500", "导入失败：" + e.getMessage());
        }
    }
}
