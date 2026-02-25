package com.atjiao.cloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ypJiao
 * * @data 2026/2/13 21:54
 * @description: TODO
 **/
@RestController
@RequestMapping("/sentineltest")
public class sentineltest {
    @GetMapping("/testA")
    public String testA(){
        return "testA";
    }

    @GetMapping("/testB")
    public String testB(){
        return "testB";
    }
}
