package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.dto.MessageTemplateDTO;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.service.MessageTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.MessageTemplate;
import com.atjiao.cloud.service.MessageTemplateService;
import com.atjiao.cloud.mapper.MessageTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
* @author 86182
* @description 针对表【message_template】的数据库操作Service实现
* @createDate 2025-11-21 11:06:34
*/
@Service
@RequiredArgsConstructor
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate>
    implements MessageTemplateService{

    private final MessageTypeService messageTypeService;

    /**
     * 用户查询自己是否创建过消息推送模板
     */
    @Override
    public Boolean queryMessageTemplate() {
        Long userId = LoginHelper.getUserId();
        Long exists = this.baseMapper.selectCount(Wrappers.<MessageTemplate>lambdaQuery()
                .eq(MessageTemplate::getCreateBy, userId)
                .eq(MessageTemplate::getTemplateType, 1) // 1为用户创建模板
                .last("limit 1"));

        return exists != null && exists > 0;
    }

    /**
     * 查询用户创建的消息模板列表
     */
    @Override
    public List<MessageTemplate> queryMessageTemplateList() {
        return this.list(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getCreateBy, LoginHelper.getUserId())
                .eq(MessageTemplate::getTemplateType, 1)); // 1为用户创建模板
    }

    /**
     * 创建消息推送模板
     * template_type: 默认0（系统模板），1（用户创建模板）
     * type: 默认"0"（系统模板内容），"1"（草稿模板内容），"2"（用户创建的内容）
     */
    @Override
    public Boolean createMessageTemplate(MessageTemplateDTO messageTemplateDTO) {
        // 判断消息类型名称是否存在
        if (messageTemplateDTO.getMessageTypeName() != null && !messageTemplateDTO.getMessageTypeName().isEmpty()) {
            // 查询该类型名称是否已存在
            MessageType messageType = messageTypeService.getOne(
                new LambdaQueryWrapper<MessageType>()
                    .eq(MessageType::getTypeName, messageTemplateDTO.getMessageTypeName())
            );

            if (messageType == null) {
                // 类型不存在，创建新的自定义类型
                MessageType messageTypeData = new MessageType();
                messageTypeData.setTypeName(messageTemplateDTO.getMessageTypeName());
                messageTypeData.setType(1); // 自定义类型

                // 设置排序，获取当前最大排序号+1
                Optional<Integer> maxSort = messageTypeService.list()
                    .stream()
                    .map(MessageType::getSort)
                    .max(Comparator.naturalOrder());
                messageTypeData.setSort(maxSort.orElse(0) + 1);

                // 保存新的消息类型
                messageTypeService.save(messageTypeData);

                // 设置消息模板的类型ID
                messageTemplateDTO.setMessageTypeId(messageTypeData.getId());
            } else {
                // 类型已存在，直接使用已有的类型ID
                messageTemplateDTO.setMessageTypeId(messageType.getId());
            }
        }

        // 创建MessageTemplate实例并拷贝属性
        MessageTemplate messageTemplate = new MessageTemplate();
        BeanUtils.copyProperties(messageTemplateDTO, messageTemplate);

        // 设置模板类型：1为用户创建模板（默认值在数据库中为0，这里显式设置为1）
        if (messageTemplate.getTemplateType() == null) {
            messageTemplate.setTemplateType(1);
        }

        // 设置type：默认"0"为正式模板，"1"为草稿
        if (messageTemplate.getType() == null || messageTemplate.getType().isEmpty()) {
            messageTemplate.setType("0"); // 默认为正式模板
        }

        // 设置版本号，首次创建默认为1
        if (messageTemplate.getVersion() == null) {
            messageTemplate.getVersion();
        }

        // 保存消息模板
        return this.save(messageTemplate);
    }

    /**
     * 更新消息推送模板
     * 支持消息类型的修改，需要修改message_type表数据
     */
    @Override
    public Boolean updateMessageTemplate(MessageTemplateDTO messageTemplateDTO) {
        // 获取原消息模板，验证是否存在以及是否是当前用户创建的
        MessageTemplate existTemplate = this.getById(messageTemplateDTO.getId());
        if (existTemplate == null) {
            return false;
        }

        // 验证是否是创建者本人
        if (!existTemplate.getCreateBy().equals(LoginHelper.getUserId())) {
            return false;
        }

        // 更新消息类型名称
        if (messageTemplateDTO.getMessageTypeName() != null && !messageTemplateDTO.getMessageTypeName().isEmpty()) {
            messageTypeService.update(new LambdaUpdateWrapper<MessageType>()
                    .eq(MessageType::getId, messageTemplateDTO.getMessageTypeId())
                    .set(MessageType::getTypeName, messageTemplateDTO.getMessageTypeName())
            );
        }

        // 拷贝属性
        MessageTemplate messageTemplate = new MessageTemplate();
        BeanUtils.copyProperties(messageTemplateDTO, messageTemplate);

        // 更新消息模板
        return this.updateById(messageTemplate);
    }

    /**
     * 删除消息推送模板
     */
    @Override
    public Boolean deleteMessageTemplate(Long id) {
        // 获取消息模板，验证是否存在以及是否是当前用户创建的
        MessageTemplate existTemplate = this.getById(id);
        if (existTemplate == null) {
            return false;
        }

        // 验证是否是创建者本人
        if (!existTemplate.getCreateBy().equals(String.valueOf(LoginHelper.getUserId()))) {
            return false;
        }

        // 删除消息模板
        return this.removeById(id);
    }

    /**
     * 查询消息模板类型
     * 可以查询到系统消息模板类型加上自己创建的消息模板类型
     */
    @Override
    public List<MessageType> queryMessageType() {
        return messageTypeService.list(new LambdaQueryWrapper<MessageType>()
                .eq(MessageType::getCreateBy, LoginHelper.getUserId())
                .or(e -> e.eq(MessageType::getCreateBy, 1001L))); // 1001L为系统创建者ID
    }

    /**
     * 查询所有消息模板内容
     * type: "0"为正式模板内容，"1"为草稿模板内容
     */
    @Override
    public List<MessageTemplate> queryAllMessageTemplateList(String type) {
        LambdaQueryWrapper<MessageTemplate> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(type)) {
            queryWrapper.eq(MessageTemplate::getType, type);
        }

        return this.list(queryWrapper);
    }

    /**
     * 查询用户创建的草稿消息模板
     * type: "1"为草稿模板内容
     */
    @Override
    public IPage<MessageTemplate> queryDraftMessageTemplate(PageQuery pageQuery) {
        Integer templateType = 1;
        String type = "1";

        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();

        IPage<MessageTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MessageTemplate> queryWrapper = new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getCreateBy, LoginHelper.getUserId());

        if (templateType != null) {
            queryWrapper.eq(MessageTemplate::getTemplateType, templateType);
        }

        if (type != null && !type.trim().isEmpty()) {
            queryWrapper.eq(MessageTemplate::getType, type);
        }

        IPage<MessageTemplate> pageResult = this.page(page, queryWrapper);

        IPage<MessageTemplate> result = new Page<>(pageNum, pageSize, pageResult.getTotal());
        result.setRecords(pageResult.getRecords());

        return result;
    }
}




