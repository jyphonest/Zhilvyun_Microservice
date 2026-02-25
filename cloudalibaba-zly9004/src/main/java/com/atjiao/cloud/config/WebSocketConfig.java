package com.atjiao.cloud.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpointConfig;

/**
 * WebSocket配置类
 * 负责WebSocket端点的注册和服务注入
 * 
 * @author 焦叶鹏
 * @date 2025/9/9 21:24
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public ServerEndpointConfig.Configurator customConfigurator() {
        return new ServerEndpointConfig.Configurator() {
            @Override
            public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
                return applicationContext.getBean(endpointClass);
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}