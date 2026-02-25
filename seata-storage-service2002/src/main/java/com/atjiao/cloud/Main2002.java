package com.atjiao.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @author jyp
 * * @data 2025/5/10
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.atjiao.cloud.mapper")
public class Main2002 {
    public static void main(String[] args) {
        SpringApplication.run(Main2002.class, args);
    }
}