package com.atjiao.cloud.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 字典数据表
 * @TableName sys_dict_data
 */
@Data
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity{
    /**
     * 字典编码
     */
    @TableId(value = "dict_code", type = IdType.AUTO)
    private Long dictCode;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}