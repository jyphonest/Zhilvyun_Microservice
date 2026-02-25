package com.atjiao.cloud.rabbitMq.RabbitMQTest.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * * @data 2025/10/14 14:15
 * @description: 配置类
 **/
@Configuration
public class QueueTTLDelay {

    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String QUEUE_C = "QC";

    //声明队列C,并绑定到交换机Y,通过YD路由键
    @Bean("queueC")
    public Queue queueC(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }

    //绑定队列C到交换机X
    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange")DirectExchange directExchange){
        return BindingBuilder.bind(queueC).to(directExchange).with("XC");
    }
}
