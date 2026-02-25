package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.MessageTemplate;
import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.dto.MessageTemplateDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86182
* @description 针对表【message_template】的数据库操作Service
* @createDate 2025-11-21 11:06:34
*/
public interface MessageTemplateService extends IService<MessageTemplate> {

    /**
     * 用户查询自己是否创建过消息推送模板
     */
    Boolean queryMessageTemplate();

    /**
     * 查询用户创建的消息模板列表
     */
    List<MessageTemplate> queryMessageTemplateList();

    /**
     * 创建消息推送模板
     */
    Boolean createMessageTemplate(MessageTemplateDTO messageTemplateDTO);

    /**
     * 更新消息推送模板
     */
    Boolean updateMessageTemplate(MessageTemplateDTO messageTemplateDTO);

    /**
     * 删除消息推送模板
     */
    Boolean deleteMessageTemplate(Long id);

    /**
     * 查询消息模板类型
     */
    List<MessageType> queryMessageType();

    /**
     * 查询所有消息模板内容（根据type筛选）
     */
    List<MessageTemplate> queryAllMessageTemplateList(String type);

    /**
     * 查询用户创建的草稿消息模板
     */
    IPage<MessageTemplate> queryDraftMessageTemplate(PageQuery pageQuery);
}
