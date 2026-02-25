package com.atjiao.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 焦叶鹏
 * * @data 2025/7/20 17:53
 * @description: TODO
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    private String endPoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
