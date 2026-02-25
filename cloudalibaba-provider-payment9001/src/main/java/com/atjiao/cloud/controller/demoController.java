package com.atjiao.cloud.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ypJiao
 * * @data 2026/2/13 20:08
 * @description: TODO
 **/
@RestController
@RequestMapping("/pay")
@SaIgnore
public class demoController {

    @GetMapping("/test")
    public String hello(){
        return "测试网关路由转发是否正常";
    }
}
