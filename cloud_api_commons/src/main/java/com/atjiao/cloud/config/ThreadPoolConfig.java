package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池配置类
 * 用于配置线程池相关的设置
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolConfig {
    
    /**
     * 核心线程数
     */
    private Integer coreSize = 10;
    
    /**
     * 最大线程数
     */
    private Integer maxSize = 20;
    
    /**
     * 队列容量
     */
    private Integer queueCapacity = 200;
    
    /**
     * 线程空闲时间(秒)
     */
    private Integer keepAlive = 60;
    
    /**
     * 线程名前缀
     */
    private String namePrefix = "ai-tourism-";
    
    /**
     * 是否启用线程池
     */
    private Boolean enabled = true;
    
    /**
     * 是否允许核心线程超时
     */
    private Boolean allowCoreThreadTimeOut = true;
    
    /**
     * 等待所有任务结束后再关闭线程池
     */
    private Boolean waitForTasksToCompleteOnShutdown = true;
    
    /**
     * 等待时间(秒)
     */
    private Integer awaitTerminationSeconds = 60;
} 