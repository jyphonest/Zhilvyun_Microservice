package com.atjiao.cloud.config.SaTokenConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 焦叶鹏
 * @data 2025/7/10 23:31
 * @description: Sa-Token接口权限配置，使用自定义拦截器进行token验证
 **/
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 使用自定义拦截器替代原有的SaInterceptor
        registry.addInterceptor(new CustomSaTokenInterceptor())
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        // 认证相关接口（无需token验证）
                        "/userInformation/doLogin",      // 前台登录接口
                        "/userInformation/adminLogin",   // 后台登录接口
                        "/userInformation/register",     // 注册接口
                        "/userInformation/refreshToken", // 刷新token接口
                        
                        // 公开接口（无需token验证）
                        "/satokenTest/**",               // Sa-Token测试接口
                        "/public/**",                    // 其他公开接口
                        
                        // 静态资源（无需token验证）
                        "/static/**",                    // 静态资源
                        "/public/**",                    // 公共资源
                        "/favicon.ico",                  // 网站图标
                        
                        // 健康检查接口（无需token验证）
                        "/actuator/**",                  // Spring Boot Actuator
                        "/health",                       // 健康检查
                        
                        // 文档接口（无需token验证）
                        "/swagger-ui/**",                // Swagger UI
                        "/swagger-resources/**",         // Swagger资源
                        "/v2/api-docs",                  // API文档
                        "/v3/api-docs",                  // OpenAPI 3.0文档
                        
                        // 错误页面（无需token验证）
                        "/error/**"                      // 错误页面
                );
    }
}

