package com.atjiao.cloud.rabbitMq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * * @data 2025/10/14 19:45
 * @description: RabbitMQ配置类
 **/

@Configuration
public class RabbitMQConfig {

    // ===== Exchange names =====
    public static final String TOPIC_EXCHANGE = "notification.topic.exchange";
    public static final String DELAY_EXCHANGE = "delay.exchange";
    public static final String AUDIT_EXCHANGE = "audit.exchange";
    public static final String BACKUP_EXCHANGE = "backup.exchange";

    // ===== Queue names =====
    public static final String USER_NOTIFICATION_QUEUE = "user.notification.queue";
    public static final String DELAYED_USER_NOTIFICATION_QUEUE = "delayed.user.notification.queue";
    public static final String DELAYED_ALL_NOTIFICATION_QUEUE = "delayed.all.notification.queue";
    public static final String NOTIFICATION_LOG_QUEUE = "notification.log.queue";
    public static final String BACKUP_QUEUE = "backup.queue";
    public static final String OFFLINE_MESSAGE_QUEUE = "offline.message.queue";

    // ===== Routing keys =====
    public static final String RK_USER_PREFIX = "notification.user.*";
    public static final String RK_USER_ALL = "notification.user.all";
    public static final String RK_DELAYED_USER_PREFIX = "notification.delayed.user.*";
    public static final String RK_DELAYED_USER_ALL = "notification.delayed.user.all";
    public static final String RK_OFFLINE_MESSAGE = "notification.offline.message";

    // ===== Exchanges =====

    @Bean
    public Exchange backupExchange() {
        return ExchangeBuilder.fanoutExchange(BACKUP_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Exchange auditExchange() {
        return ExchangeBuilder.fanoutExchange(AUDIT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Exchange topicExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", BACKUP_EXCHANGE);
        return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE)
                .durable(true)
                .withArguments(args)
                .build();
    }

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic");
        return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    // ===== Queues =====

    @Bean
    public Queue userNotificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", BACKUP_EXCHANGE);
        return QueueBuilder.durable(USER_NOTIFICATION_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue delayedUserNotificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", BACKUP_EXCHANGE);
        return QueueBuilder.durable(DELAYED_USER_NOTIFICATION_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue delayedAllNotificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", BACKUP_EXCHANGE);
        return QueueBuilder.durable(DELAYED_ALL_NOTIFICATION_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue notificationLogQueue() {
        return QueueBuilder.durable(NOTIFICATION_LOG_QUEUE).build();
    }

    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }

    @Bean
    public Queue offlineMessageQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", BACKUP_EXCHANGE);
        return QueueBuilder.durable(OFFLINE_MESSAGE_QUEUE).withArguments(args).build();
    }

    // ===== Bindings =====

    // --- 即时消息 ---
    @Bean
    public Binding bindTopicToUserQueue() {
        return BindingBuilder.bind(userNotificationQueue())
                .to(topicExchange())
                .with(RK_USER_PREFIX)
                .noargs();
    }

    @Bean
    public Binding bindTopicToUserAll() {
        return BindingBuilder.bind(userNotificationQueue())
                .to(topicExchange())
                .with(RK_USER_ALL)
                .noargs();
    }

    // --- 延迟消息 ---
    @Bean
    public Binding bindDelayToUserQueue() {
        return BindingBuilder.bind(delayedUserNotificationQueue())
                .to(delayExchange())
                .with(RK_DELAYED_USER_PREFIX)
                .noargs();
    }

    @Bean
    public Binding bindDelayToAllQueue() {
        return BindingBuilder.bind(delayedAllNotificationQueue())
                .to(delayExchange())
                .with(RK_DELAYED_USER_ALL)
                .noargs();
    }

    // --- 审计日志绑定 --- 暂时注销这两个绑定,持久化消息只需要持久化单条数据即可
    // @Bean
    // public Binding bindTopicToAuditExchange() {
    //     return BindingBuilder.bind(auditExchange())
    //             .to(topicExchange())
    //             .with("notification.#")
    //             .noargs();
    // }
    //
    // @Bean
    // public Binding bindDelayToAuditExchange() {
    //     return BindingBuilder.bind(auditExchange())
    //             .to(delayExchange())
    //             .with("notification.#")
    //             .noargs();
    // }

    @Bean
    public Binding bindAuditToLogQueue() {
        return BindingBuilder.bind(notificationLogQueue())
                .to(auditExchange())
                .with("")
                .noargs();
    }

    // --- 备份队列绑定 ---
    @Bean
    public Binding bindBackupExchangeToQueue() {
        return BindingBuilder.bind(backupQueue())
                .to(backupExchange())
                .with("")
                .noargs();
    }

    // --- 离线消息队列绑定 ---
    @Bean
    public Binding bindTopicToOfflineQueue() {
        return BindingBuilder.bind(offlineMessageQueue())
                .to(topicExchange())
                .with(RK_OFFLINE_MESSAGE)
                .noargs();
    }

    // ===== Listener container factory =====
    @Bean
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置消费者并发数量
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        // 设置预取数量，避免消息积压
        factory.setPrefetchCount(1);
        // 启用事务支持
        factory.setChannelTransacted(true);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        // 启用事务支持
        template.setChannelTransacted(true);
        return template;
    }
}
