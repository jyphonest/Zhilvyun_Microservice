package com.atjiao.cloud.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.atjiao.cloud.service.OfflineMessageService;
import com.atjiao.cloud.util.FastJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 焦叶鹏
 * @description 离线消息服务实现类
 * @createDate 2025-10-21
 */
@Slf4j
@Service
public class OfflineMessageServiceImpl implements OfflineMessageService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户的离线消息ID列表
     * @param userId 用户ID
     * @return 消息ID列表
     */
    @Override
    public List<Long> getOfflineMessageIds(Long userId) {
        try {
            String key = "offline:msg:ids:" + userId;
            // 获取List中的所有元素
            List<Object> messageObjects = redisTemplate.opsForList().range(key, 0, -1);
            if (messageObjects != null) {
                List<Long> messageIds = new ArrayList<>();
                for (Object obj : messageObjects) {
                    if (obj instanceof Long) {
                        messageIds.add((Long) obj);
                    } else if (obj instanceof String) {
                        messageIds.add(Long.parseLong((String) obj));
                    } else if (obj instanceof Integer) {
                        messageIds.add(((Integer) obj).longValue());
                    }
                }
                return messageIds;
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户[{}]离线消息ID列表失败: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据消息ID获取消息内容
     * @param messageId 消息ID
     * @return 消息内容
     */
    @Override
    public JSONObject getOfflineMessageContent(Long messageId) {
        try {
            String key = "offline:msg:content";
            Object messageJson = redisTemplate.opsForHash().get(key, String.valueOf(messageId));
            if (messageJson != null) {
                return FastJsonUtil.parseSafe(messageJson.toString());
            }
            return null;
        } catch (Exception e) {
            log.error("获取消息[{}]内容失败: {}", messageId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除用户的离线消息ID（用户上线后已推送的消息）
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    @Override
    public void removeOfflineMessageId(Long userId, Long messageId) {
        try {
            String key = "offline:msg:ids:" + userId;
            // 从List中移除指定的消息ID
            redisTemplate.opsForList().remove(key, 1, messageId);
        } catch (Exception e) {
            log.error("删除用户[{}]离线消息ID[{}]失败: {}", userId, messageId, e.getMessage(), e);
        }
    }

    /**
     * 批量删除用户的离线消息ID
     * @param userId 用户ID
     * @param messageIds 消息ID列表
     */
    @Override
    public void removeOfflineMessageIds(Long userId, List<Long> messageIds) {
        try {
            String key = "offline:msg:ids:" + userId;
            // 批量删除List中的消息ID
            for (Long messageId : messageIds) {
                redisTemplate.opsForList().remove(key, 1, messageId);
            }
        } catch (Exception e) {
            log.error("批量删除用户[{}]离线消息ID失败: {}", userId, e.getMessage(), e);
        }
    }
}