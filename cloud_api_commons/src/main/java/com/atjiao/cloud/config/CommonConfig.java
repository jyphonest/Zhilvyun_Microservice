package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 通用配置类
 * 用于加载和管理AI旅游云平台的通用配置项
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "business")
public class CommonConfig {
    
    /**
     * 旅游相关配置
     */
    private Travel travel = new Travel();
    
    /**
     * 用户相关配置
     */
    private User user = new User();
    
    /**
     * 旅游配置
     */
    @Data
    public static class Travel {
        /**
         * 最大计划数量
         */
        private Integer maxPlanCount = 100;
        
        /**
         * 最大攻略长度
         */
        private Integer maxStrategyLength = 10000;
        
        /**
         * 默认预算
         */
        private Integer defaultBudget = 5000;
        
        /**
         * 默认天数
         */
        private Integer defaultDays = 7;
    }
    
    /**
     * 用户配置
     */
    @Data
    public static class User {
        /**
         * 密码最小长度
         */
        private Integer passwordMinLength = 6;
        
        /**
         * 密码最大长度
         */
        private Integer passwordMaxLength = 20;
        
        /**
         * 用户名最小长度
         */
        private Integer usernameMinLength = 3;
        
        /**
         * 用户名最大长度
         */
        private Integer usernameMaxLength = 20;
        
        /**
         * 邮箱验证码有效期(分钟)
         */
        private Integer emailCodeExpire = 5;
        
        /**
         * 手机验证码有效期(分钟)
         */
        private Integer phoneCodeExpire = 5;
    }
} 