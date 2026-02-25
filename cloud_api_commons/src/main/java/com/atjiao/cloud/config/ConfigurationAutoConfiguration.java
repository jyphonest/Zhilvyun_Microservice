package com.atjiao.cloud.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置自动装配类
 * 用于自动装配AI旅游云平台的所有配置类
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Configuration
@EnableConfigurationProperties({
    CommonConfig.class,
    CacheConfig.class,
    FileUploadConfig.class,
    CorsConfig.class,
    ThreadPoolConfig.class
})
@ConditionalOnProperty(prefix = "ai-tourism", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConfigurationAutoConfiguration {
    
    /**
     * 配置模块启用标志
     * 可以通过 ai-tourism.enabled=false 来禁用整个配置模块
     */
    private static final String MODULE_NAME = "AI旅游云平台通用配置模块";
    
    /**
     * 模块描述
     */
    private static final String MODULE_DESCRIPTION = "提供Sa-Token认证、Spring Security安全、数据库配置、MyBatis-Plus配置、日志配置等通用功能";
    
    /**
     * 版本信息
     */
    private static final String VERSION = "1.0.0";
} 