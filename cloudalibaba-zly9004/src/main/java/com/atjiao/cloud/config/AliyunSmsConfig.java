package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 焦叶鹏
 * @since 2025/1/4
 * @description: 阿里云短信服务配置类
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String signName;
    private String templateCode;
}