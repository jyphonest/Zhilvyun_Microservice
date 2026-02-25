package com.atjiao.cloud.rabbitMq.RabbitMQTest.consumer;

import org.springframework.stereotype.Component;


/**
 * @author 焦叶鹏
 * * @data 2025/10/10 15:30
 * @description: 消费者类2
 **/

@Component
public class RabbitConsumer2 {

    // @RabbitListener(queues = QUEUE_NAME,containerFactory = "prefetch2Factory")
    // public void handleMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws InterruptedException, IOException {
    //     System.out.println("consumer2收到消息时间较长");
    //
    //     // 模拟处理消息的延迟
    //     Thread.sleep(1000*10);
    //
    //     // 打印消息内容（直接接收消息体字符串，无需手动解析）
    //     try {
    //         System.out.println("consumer2成功消费消息!内容为:" + message);
    //         //手动确认
    //         channel.basicAck(deliveryTag,false);
    //     }catch (Exception e){
    //         channel.basicNack(deliveryTag,false,true);
    //     }
    //
    // }
}
