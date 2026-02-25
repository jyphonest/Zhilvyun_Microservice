package com.atjiao.cloud.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jyp
 * * @data 2025/5/7
 **/
@RestController
@RefreshScope //在控制器类加入@RefreshScope注解使当前类下的配置支持Nacos的动态刷新功能。
public class NacosConfigClientController
{
    @Value("${config.info}")
    private String configInfo;

    @Value("${sa-token.token-name}")
    private String configInfo2;

    @GetMapping("/config/info")
    public String getConfigInfo() {
        return configInfo;
    }

    @GetMapping("/config/info2")
    public String getConfigInfo2() {
        return configInfo2;
    }
}
