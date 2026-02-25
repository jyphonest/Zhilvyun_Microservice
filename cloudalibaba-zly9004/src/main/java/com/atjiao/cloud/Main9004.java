package com.atjiao.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 焦叶鹏
 * * @data 2025/7/15 20:41
 * @description: TODO
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan("com.atjiao.cloud.mapper")
@EnableConfigurationProperties
public class Main9004 {
    public static void main(String[] args) {
        SpringApplication.run(Main9004.class, args);
    }
}