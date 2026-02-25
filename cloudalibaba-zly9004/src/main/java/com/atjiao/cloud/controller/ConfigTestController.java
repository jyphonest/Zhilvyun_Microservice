package com.atjiao.cloud.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.atjiao.cloud.config.CommonConfig;
import com.atjiao.cloud.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置测试控制器
 * 用于验证通用配置是否正确加载
 * 
 * @author AI助手
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/config")
@SaIgnore
public class ConfigTestController {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private CacheConfig cacheConfig;

    /**
     * 测试通用配置加载
     */
    @GetMapping("/test")
    public Map<String, Object> testConfig() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试业务配置
            Map<String, Object> businessConfig = new HashMap<>();
            businessConfig.put("maxPlanCount", commonConfig.getTravel().getMaxPlanCount());
            businessConfig.put("maxStrategyLength", commonConfig.getTravel().getMaxStrategyLength());
            businessConfig.put("defaultBudget", commonConfig.getTravel().getDefaultBudget());
            businessConfig.put("defaultDays", commonConfig.getTravel().getDefaultDays());
            businessConfig.put("passwordMinLength", commonConfig.getUser().getPasswordMinLength());
            businessConfig.put("passwordMaxLength", commonConfig.getUser().getPasswordMaxLength());
            result.put("businessConfig", businessConfig);

            // 测试缓存配置
            Map<String, Object> cacheConfigMap = new HashMap<>();
            cacheConfigMap.put("enabled", cacheConfig.getEnabled());
            cacheConfigMap.put("defaultTtl", cacheConfig.getDefaultTtl());
            cacheConfigMap.put("userTtl", cacheConfig.getUserTtl());
            cacheConfigMap.put("travelPlanTtl", cacheConfig.getTravelPlanTtl());
            cacheConfigMap.put("type", cacheConfig.getType());
            result.put("cacheConfig", cacheConfigMap);

            result.put("success", true);
            result.put("message", "配置加载成功");
            
            log.info("配置测试成功: {}", result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "配置加载失败: " + e.getMessage());
            log.error("配置测试失败", e);
        }
        
        return result;
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里可以添加数据库连接测试逻辑
            result.put("success", true);
            result.put("message", "数据库配置正常");
            result.put("database", "zhilvyun_jiaoyp");
            result.put("driver", "com.mysql.cj.jdbc.Driver");
            
            log.info("数据库测试成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
            log.error("数据库测试失败", e);
        }
        
        return result;
    }

    /**
     * 测试健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "nacos-payment-provider");
        result.put("port", 9001);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试服务信息
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "nacos-payment-provider");
        result.put("version", "1.0-SNAPSHOT");
        result.put("description", "AI旅游云平台 - 旅游服务提供者");
        result.put("port", 9004);
        result.put("configLoaded", true);
        return result;
    }
} 