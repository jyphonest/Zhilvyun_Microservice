package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.UserMessage;
import com.atjiao.cloud.domain.vo.UserMessageVo;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

/**
* @author 86182
* @description 针对表【user_message(用户-消息关联表)】的数据库操作Service
* @createDate 2025-10-16 18:56:49
*/
public interface UserMessageService extends IService<UserMessage> {

    /**
     * 更新阅读状态
     */
        void updateReadStatus(Long userId);

    /**
     * 更新发送状态
     * @param userMessageId 用户消息ID
     * @param sendStatus 发送状态 (1:已发送, 0:未发送)
     */
    void updateSendStatus(Long userMessageId, Integer sendStatus);

    /**
     * 2.用户查询自己的消息信息
     */
    List<UserMessageVo>  queryMessageInfo();

    /**
     * 3.用户删除自己的消息
     */
    void deleteMessage(Long id);

    /**
     * 4.用户批量删除消息
     * @param ids 消息ID列表
     */
    void deleteMessages(List<Long> ids);
}
