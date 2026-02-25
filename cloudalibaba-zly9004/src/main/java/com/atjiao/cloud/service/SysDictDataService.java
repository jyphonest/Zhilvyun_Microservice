package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.SysDictData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86182
* @description 针对表【sys_dict_data】的数据库操作Service
* @createDate 2025-11-12 23:09:45
*/
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 新增字典数据
     * @param sysDictData 字典数据
     * @return 是否成功
     */
    boolean saveDict(SysDictData sysDictData);

    /**
     * 更新字典数据
     * @param sysDictData 字典数据
     * @return 是否成功
     */
    boolean updateDict(SysDictData sysDictData);

    /**
     * 删除字典数据（逻辑删除）
     * @param dictCode 字典编码
     * @return 是否成功
     */
    boolean deleteDict(Long dictCode);

    /**
     * 根据ID查询字典数据
     * @param dictCode 字典编码
     * @return 字典数据
     */
    SysDictData getDictById(Long dictCode);

    /**
     * 分页查询字典数据
     * @param sysDictData 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    IPage<SysDictData> queryDictList(SysDictData sysDictData, PageQuery pageQuery);

    /**
     * 根据字典类型查询字典数据列表
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    java.util.List<SysDictData> queryDictByType(String dictType);

    /**
     * 从枚举类导入字典数据到数据库
     * @param enumClass 枚举类的Class对象
     * @param dictType 字典类型
     * @return 导入成功的记录数
     */
    int importDictFromEnum(Class<? extends Enum<?>> enumClass, String dictType);
}
