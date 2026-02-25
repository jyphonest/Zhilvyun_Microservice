package com.atjiao.cloud.service;

import com.alibaba.fastjson2.JSONObject;
import java.util.List;

/**
 * @author 焦叶鹏
 * @description 离线消息服务接口
 * @createDate 2025-10-21
 */
public interface OfflineMessageService {

    /**
     * 获取用户的离线消息ID列表
     * @param userId 用户ID
     * @return 消息ID列表
     */
    List<Long> getOfflineMessageIds(Long userId);

    /**
     * 根据消息ID获取消息内容
     * @param messageId 消息ID
     * @return 消息内容
     */
    JSONObject getOfflineMessageContent(Long messageId);

    /**
     * 删除用户的离线消息ID（用户上线后已推送的消息）
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void removeOfflineMessageId(Long userId, Long messageId);

    /**
     * 批量删除用户的离线消息ID
     * @param userId 用户ID
     * @param messageIds 消息ID列表
     */
    void removeOfflineMessageIds(Long userId, List<Long> messageIds);
}