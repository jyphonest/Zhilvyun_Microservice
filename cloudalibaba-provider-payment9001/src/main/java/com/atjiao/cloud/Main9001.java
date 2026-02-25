package com.atjiao.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 旅游服务提供者启动类
 * @author jyp
 * @data 2025/5/7
 **/
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.atjiao.cloud.mapper")
public class Main9001 {
    public static void main(String[] args) {
        SpringApplication.run(Main9001.class, args);
    }
}