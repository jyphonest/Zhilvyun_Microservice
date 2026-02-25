package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 跨域配置类
 * 用于配置CORS相关的设置
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsConfig {
    
    /**
     * 允许的域名
     */
    private String allowedOrigins = "*";
    
    /**
     * 允许的方法
     */
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
    
    /**
     * 允许的请求头
     */
    private String allowedHeaders = "*";
    
    /**
     * 是否允许携带凭证
     */
    private Boolean allowCredentials = true;
    
    /**
     * 预检请求的有效期(秒)
     */
    private Integer maxAge = 3600;
    
    /**
     * 是否启用跨域
     */
    private Boolean enabled = true;
}

