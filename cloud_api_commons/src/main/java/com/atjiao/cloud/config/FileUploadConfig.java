package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置类
 * 用于配置文件上传相关的设置
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 上传路径
     */
    private String path = "/upload/";
    
    /**
     * 最大文件大小
     */
    private String maxSize = "10MB";
    
    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx";
    
    /**
     * 是否使用OSS
     */
    private Boolean useOss = false;
    
    /**
     * 是否使用原始文件名
     */
    private Boolean useOriginalFilename = false;
    
    /**
     * 文件存储类型: local, oss, cos
     */
    private String storageType = "local";
    
    /**
     * 是否启用文件上传
     */
    private Boolean enabled = true;
} 