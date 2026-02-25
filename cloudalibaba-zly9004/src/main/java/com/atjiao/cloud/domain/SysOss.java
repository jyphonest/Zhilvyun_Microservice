package com.atjiao.cloud.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 
 * @TableName sys_oss
 */
@Data
public class SysOss extends BaseEntity {
    /**
     * 对象存储主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 服务商
     */
    private String service;

    /**
     * 业务模块标识
     */
    private String bizType;

    /**
     * 关联业务主键
     */
    private Long bizId;

    /**
     * 文件顺序
     */
    private Integer sort;


}