package com.atjiao.cloud.rabbitMq.RabbitMQTest.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 焦叶鹏
 * * @data 2025/10/9 09:59
 * @description: 生产者类
 **/

@Component
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendFanoutMessage() {

    }
}


