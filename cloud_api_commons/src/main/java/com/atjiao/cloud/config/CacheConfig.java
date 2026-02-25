package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 用于配置缓存相关的设置
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {
    
    /**
     * 默认缓存时间(秒)
     */
    private Integer defaultTtl = 3600;
    
    /**
     * 用户信息缓存时间(秒)
     */
    private Integer userTtl = 1800;
    
    /**
     * 旅游计划缓存时间(秒)
     */
    private Integer travelPlanTtl = 7200;
    
    /**
     * 验证码缓存时间(秒)
     */
    private Integer codeTtl = 300;
    
    /**
     * 权限信息缓存时间(秒)
     */
    private Integer permissionTtl = 3600;
    
    /**
     * 字典数据缓存时间(秒)
     */
    private Integer dictTtl = 86400;
    
    /**
     * 是否启用缓存
     */
    private Boolean enabled = true;
    
    /**
     * 缓存类型: redis, caffeine, ehcache
     */
    private String type = "redis";
} 