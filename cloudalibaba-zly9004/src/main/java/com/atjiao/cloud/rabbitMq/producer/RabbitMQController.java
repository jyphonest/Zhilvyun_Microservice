package com.atjiao.cloud.rabbitMq.producer;


import cn.hutool.core.date.DateTime;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.rabbitMq.config.RabbitMQConfig;
import com.atjiao.cloud.resp.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * * @data 2025/10/14 19:48
 * @description: RabbitMQ生产者
 **/
@RestController
@RequestMapping("/rabbitmq")
@RequiredArgsConstructor
@Slf4j
public class RabbitMQController {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送即时消息
     * @param userId 用户ID（可以为 "all" 表示全体）
     * @param content 消息内容
     */
    @PostMapping("/sendNow")
    public String sendNow(@RequestParam String userId,
                          @RequestParam String content,
                          @RequestParam String title,
                          @RequestParam String messageType,
                          @RequestParam String pushTime) {
        String routingKey = userId.equals("all")
                ? RabbitMQConfig.RK_USER_ALL
                : "notification.user." + userId;
        Long realUserId = LoginHelper.getUserId();
        log.info("当前登录的用户id：{}", realUserId);
        Map<String, Object> message = new HashMap<>();
        message.put("type", "instant");
        message.put("userId", userId);
        message.put("content", content);
        message.put("title", title);
        message.put("messageType", messageType);
        message.put("pushTime", pushTime);
        message.put("timestamp", System.currentTimeMillis());
        message.put("realUserId", realUserId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                routingKey,
                message
        );

        return "发送即时消息到用户：" + userId;
    }

    /**
     * 发送延迟消息
     * @param userId 用户ID（可以为 "all" 表示全体）
     * @param content 消息内容
     */
    @PostMapping("/sendDelay")
    public String sendDelay(
            @RequestParam String userId,
            @RequestParam String content,
            @RequestParam String title,
            @RequestParam String messageType,
            @RequestParam String pushTime
    ) {
        // 1. 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 2. 解析 pushTime 并计算 delay 毫秒数
        long delay;
        try {
            LocalDateTime scheduledTime = LocalDateTime.parse(pushTime, formatter);
            LocalDateTime now = LocalDateTime.now();

            // 计算两个时间点之间的毫秒数
            // 注意：ChronoUnit.MILLIS.between() 方法适用于较小的时间间隔，
            // 对于跨度较大的延迟消息，使用 ZoneId 转换为 epoch 毫秒更健壮。

            // 转换为系统默认时区的毫秒时间戳
            long scheduledMillis = scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            delay = scheduledMillis - nowMillis;

            if (delay <= 0) {
                return "错误：pushTime (" + pushTime + ") 必须是未来的时间点。";
            }

        } catch (java.time.format.DateTimeParseException e) {
            return "错误：pushTime 格式不正确，期望格式为 yyyy-MM-dd HH:mm:ss";
        } catch (Exception e) {
            return "发送失败: " + e.getMessage();
        }

        // 3. 构建 routingKey 和消息体 (使用计算出的 delay 值)
        String routingKey = userId.equals("all")
                ? RabbitMQConfig.RK_DELAYED_USER_ALL
                : "notification.delayed.user." + userId;

        Long realUserId = LoginHelper.getUserId();

        Map<String, Object> message = new HashMap<>();
        message.put("type", "delayed");
        message.put("userId", userId);
        message.put("content", content);
        message.put("delay", (int) delay);
        message.put("timestamp", System.currentTimeMillis());
        message.put("title", title);
        message.put("messageType", messageType);
        message.put("pushTime", pushTime);
        message.put("realUserId", realUserId);

        // 4. 发送消息
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                routingKey,
                message,
                m -> {
                    m.getMessageProperties().setDelay((int) delay);
                    return m;
                }
        );

        return "发送延时消息到用户：" + userId + " (延迟 " + delay + "毫秒, 计划发送时间：" + pushTime + ")";
    }
}

