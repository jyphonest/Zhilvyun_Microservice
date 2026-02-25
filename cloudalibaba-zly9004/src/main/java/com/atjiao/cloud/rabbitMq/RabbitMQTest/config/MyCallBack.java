package com.atjiao.cloud.rabbitMq.RabbitMQTest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 焦叶鹏
 * * @data 2025/10/14 15:47
 * @description: 回调接口
 **/
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 将我们实现的MyCallBack接口注入到RabbitTemplate中
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 交换机确认回调方法
     *
     * @param correlationData 保存回调消息的ID以及相关信息
     * @param ack             表示交换机是否收到消息(true表示收到)
     * @param cause           表示消息接收失败的原因(收到消息为null)
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到ID为:{}的消息", id);
        } else {
            log.info("交换机还未收到ID为:{}的消息,原因为:{}", id, cause);
        }
    }
}
