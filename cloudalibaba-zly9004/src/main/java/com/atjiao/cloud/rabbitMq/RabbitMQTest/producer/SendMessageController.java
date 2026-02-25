package com.atjiao.cloud.rabbitMq.RabbitMQTest.producer;

import com.atjiao.cloud.rabbitMq.RabbitMQTest.config.Confirm;
import com.atjiao.cloud.rabbitMq.RabbitMQTest.config.PluginsDelay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author 焦叶鹏
 * * @data 2025/10/13 21:20
 * @description: 生产者控制层
 **/
@RestController
@RequestMapping("/sendMessage")
@Slf4j
public class SendMessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendMsg/{message}")
    public void sendMsg1(@PathVariable String message) {
        log.info("当前时间:{},发送一条信息给两个TTL队列:{}", new Date(), message);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl=10s的队列" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl=40s的队列" + message);
    }

    @GetMapping("sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg2(@PathVariable String message, @PathVariable String ttlTime) {
        /**
         * 第四个参数为消息后置处理器,用于设置消息的额外属性
         */
        rabbitTemplate.convertAndSend("X", "XC", message, correlationData -> {
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });
        log.info("当前时间:{},发送一条时长{}毫秒TTL信息给队列C:{}", new Date(), ttlTime, message);
    }

    @GetMapping("sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message, @PathVariable Integer delayTime) {
        rabbitTemplate.convertAndSend(PluginsDelay.DELAYED_EXCHANGE_NAME, PluginsDelay.DELAYED_ROUTING_KEY, message, correlationData -> {
            correlationData.getMessageProperties().setDelay(delayTime);
            return correlationData;
        });
        log.info("当前时间:{},发送一条延迟{}毫秒的信息给队列delayed.queue:{}", new Date(), delayTime, message);
    }

    @GetMapping("/confirm/sendMes/{message}")
    public void sendConfirmMessage(@PathVariable String message) {
        // 指定消息的id为1
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(Confirm.CONFIRM_EXCHANGE_NAME, "unkonwn", message, correlationData1);
        log.info("发送消息内容为:{}", message);
    }

}
