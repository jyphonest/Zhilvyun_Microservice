package com.atjiao.cloud.controller;

import com.atjiao.cloud.apis.PayFeignSentinelApi;
import com.atjiao.cloud.resp.ResultData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author jyp
 * * @data 2025/4/1
 **/
@RestController
public class OrderNacosController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-service}")
    private String serviceUrl;

    @GetMapping("/consumer/pay/nacos/{id}")
    public String paymentInfo(@PathVariable("id") Integer id){
        String result = restTemplate.getForObject(serviceUrl+"/payment/nacos/{id}",String.class,id);
        return result+"\t"+"    我是OrderNacosController83调用者。。。。。。";
    }

    @Resource
    PayFeignSentinelApi payFeignSentinelApi;

    @GetMapping(value = "/consumer/pay/nacos/get/{orderNo}")
    public ResultData getPayByOrderNo(@PathVariable("orderNo") String orderNo)
    {
        return payFeignSentinelApi.getPayByOrderNo(orderNo);
    }
}