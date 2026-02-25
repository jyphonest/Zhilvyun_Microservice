package com.atjiao.cloud.rabbitMq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ypJiao
 * @data 2025/12/29 15:15
 * @description: RabbitMQ聊天消息配置类
 **/
@Configuration
public class RabbitMQChatConfig {

    // ===== Exchange names =====
    public static final String CHAT_MSG_EXCHANGE = "chat.msg.exchange";
    public static final String CHAT_MSG_DEAD_EXCHANGE = "chat.msg.dead.exchange";

    // ===== Queue names =====
    public static final String CHAT_MSG_PERSIST_QUEUE = "chat.msg.persist.queue";
    public static final String CHAT_MSG_PUSH_QUEUE = "chat.msg.push.queue";
    public static final String CHAT_MSG_DEAD_QUEUE = "chat.msg.dead.queue";

    // ===== Routing keys =====
    public static final String RK_CHAT_MSG_SEND = "chat.msg.send";

    // ===== Exchanges =====

    /**
     * 聊天消息主题交换机
     */
    @Bean
    public Exchange chatMsgExchange() {
        return ExchangeBuilder.topicExchange(CHAT_MSG_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 死信交换机
     */
    @Bean
    public Exchange chatMsgDeadExchange() {
        return ExchangeBuilder.directExchange(CHAT_MSG_DEAD_EXCHANGE)
                .durable(true)
                .build();
    }

    // ===== Queues =====

    /**
     * 持久化队列（配置死信队列）
     */
    @Bean
    public Queue chatMsgPersistQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", CHAT_MSG_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", "dead");
        return QueueBuilder.durable(CHAT_MSG_PERSIST_QUEUE)
                .withArguments(args)
                .build();
    }

    /**
     * 实时推送队列（配置死信队列）
     */
    @Bean
    public Queue chatMsgPushQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", CHAT_MSG_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", "dead");
        return QueueBuilder.durable(CHAT_MSG_PUSH_QUEUE)
                .withArguments(args)
                .build();
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue chatMsgDeadQueue() {
        return QueueBuilder.durable(CHAT_MSG_DEAD_QUEUE).build();
    }

    // ===== Bindings =====

    /**
     * 持久化队列绑定到主题交换机
     */
    @Bean
    public Binding bindChatMsgPersistQueue() {
        return BindingBuilder.bind(chatMsgPersistQueue())
                .to(chatMsgExchange())
                .with(RK_CHAT_MSG_SEND)
                .noargs();
    }

    /**
     * 实时推送队列绑定到主题交换机
     */
    @Bean
    public Binding bindChatMsgPushQueue() {
        return BindingBuilder.bind(chatMsgPushQueue())
                .to(chatMsgExchange())
                .with(RK_CHAT_MSG_SEND)
                .noargs();
    }

    /**
     * 死信队列绑定到死信交换机
     */
    @Bean
    public Binding bindChatMsgDeadQueue() {
        return BindingBuilder.bind(chatMsgDeadQueue())
                .to(chatMsgDeadExchange())
                .with("dead")
                .noargs();
    }
}
