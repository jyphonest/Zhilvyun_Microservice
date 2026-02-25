package com.atjiao.cloud.rabbitMq.RabbitMQTest.consumer;

import com.atjiao.cloud.rabbitMq.RabbitMQTest.config.Confirm;
import com.atjiao.cloud.rabbitMq.RabbitMQTest.config.PluginsDelay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.Date;

/**
 * @author 焦叶鹏
 * * @data 2025/10/13 21:23
 * @description: 消费者
 **/
@Component
@Slf4j
public class MessageConsumer {
    @RabbitListener(queues = "QD")
    public void receiveD(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        log.info("当前时间:{},收到死信队列信息{}", new Date().toString(), msg);
    }

    @RabbitListener(queues = PluginsDelay.DELAYED_QUEUE_NAME)
    public void receiveDelayedQueue(Message message) {
        String msg = new String(message.getBody());
        log.info("当前时间：{},收到延时队列的消息：{}", new Date().toString(), msg);
    }

    @RabbitListener(queues = Confirm.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message) {
        String msg = new String(message.getBody());
        log.info("接收到的队列confirm.queue的消息为:{}", msg);
    }

    @RabbitListener(queues = Confirm.WARNING_QUEUE_NAME)
    public void receiveWarningMsg(Message message) {
        String msg = new String(message.getBody());
        log.error("报警发现不可路由消息：{}", msg);
    }




}
