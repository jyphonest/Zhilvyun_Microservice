package com.atjiao.cloud.service.impl;

import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.dto.SysMessageDto;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.service.MessageTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.SysMessage;
import com.atjiao.cloud.service.MessageService;
import com.atjiao.cloud.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
* @author 86182
* @description 针对表【message(消息推送主表)】的数据库操作Service实现
* @createDate 2025-10-16 18:56:04
*/
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, SysMessage>
    implements MessageService{

    private final MessageTypeService messageTypeService;

    /**
     * 用户查询自己是否创建过消息推送模板
     */
    @Override
    public  Boolean queryMessageTemplate(){
        Long userId = LoginHelper.getUserId();
        Long exists = this.baseMapper.selectCount(Wrappers.<SysMessage>lambdaQuery()
                .eq(SysMessage::getSenderId, userId)
                .last("limit 1"));

        return exists != null && exists > 0 ;
    }


    /**
     * 查询用户创建的消息模板
     */
    @Override
    public List<SysMessage> queryMessageTemplateList() {
        return this.list(new LambdaQueryWrapper<SysMessage>().eq(SysMessage::getSenderId, LoginHelper.getUserId()));
    }

    /**
     * 创建消息推送模板
     */
    @Override
    public Boolean createMessageTemplate(SysMessageDto sysMessage) {
        // 设置发送者ID为当前登录用户
        sysMessage.setSenderId(LoginHelper.getUserId());

        // 判断消息类型名称是否存在
        if(sysMessage.getMessageTypeName() != null && !sysMessage.getMessageTypeName().isEmpty()){
            // 查询该类型名称是否已存在
            MessageType messageType = messageTypeService.getOne(
                new LambdaQueryWrapper<MessageType>()
                    .eq(MessageType::getTypeName, sysMessage.getMessageTypeName())
            );

            if(messageType == null){
                // 类型不存在，创建新的自定义类型
                MessageType messageTypeData = new MessageType();
                messageTypeData.setTypeName(sysMessage.getMessageTypeName());
                messageTypeData.setType(1);

                // 设置排序，获取当前最大排序号+1
                Optional<Integer> maxSort = messageTypeService.list()
                    .stream()
                    .map(MessageType::getSort)
                    .max(Comparator.naturalOrder());
                messageTypeData.setSort(maxSort.orElse(0) + 1);

                // 保存新的消息类型
                messageTypeService.save(messageTypeData);

                // 设置消息模板的类型ID和类型
                sysMessage.setMessageTypeId(messageTypeData.getId());
                sysMessage.setType(2); // 自定义类型
            } else {
                // 类型已存在，直接使用已有的类型ID
                sysMessage.setMessageTypeId(messageType.getId());
                // 根据messageType的type设置对应的type
                if(sysMessage.getType() == null){
                    sysMessage.setType(messageType.getType());
                }
            }
        }

        // 处理effectiveTime字段
        // 如果前端未传入effectiveTime且选择立即发送，则设置为当前时间
        if(sysMessage.getEffectiveTime() == null && sysMessage.getPushStatus() != null && sysMessage.getPushStatus() == 1){
            sysMessage.setEffectiveTime(new java.util.Date());
        }

        // 处理pushStatus和type的逻辑
        // 只有当消息模板已存在时，才需要判断type的转换
        if(sysMessage.getId() != null){
            // 查询已存在的消息模板
            SysMessage existMessage = this.getById(sysMessage.getId());
            // 如果已存在的模板type=3(草稿内容)，且用户选择立即发送(pushStatus=1)，则更新type为2(用户自定义模板内容)
            if(existMessage != null && existMessage.getType() != null && existMessage.getType() == 3){
                if(sysMessage.getPushStatus() != null && sysMessage.getPushStatus() == 1){
                    sysMessage.setType(2);
                }
            }
        }

        // 创建SysMessage实例并拷贝属性
        SysMessage copySysMessage = new SysMessage();
        BeanUtils.copyProperties(sysMessage, copySysMessage);

        // 设置推送状态，使用前端传入的值，如果未传入则默认为0（草稿箱）
        if(copySysMessage.getPushStatus() == null){
            copySysMessage.setPushStatus(0);
        }

        // 保存消息模板
        return this.save(copySysMessage);
    }

    /**
     * 更新消息推送模板,支持消息类型的修改，需要修改message_type表数据
     */
    @Override
    public Boolean updateMessageTemplate(SysMessageDto sysMessage) {
        // 获取原消息模板，验证是否存在以及是否是当前用户创建的
        SysMessage existMessage = this.getById(sysMessage.getId());
        if (existMessage == null) {
            return false;
        }
        // 验证是否是创建者本人
        if (!existMessage.getSenderId().equals(LoginHelper.getUserId())) {
            return false;
        }
        //更新类型
        if(sysMessage.getMessageTypeName() != null && !sysMessage.getMessageTypeName().isEmpty()){
            messageTypeService.update(new LambdaUpdateWrapper<MessageType>()
                    .eq(MessageType::getId,sysMessage.getMessageTypeId())
                    .set(MessageType::getTypeName,sysMessage.getMessageTypeName())
            );
        }

        SysMessage copySysMessage = new SysMessage();
        BeanUtils.copyProperties(sysMessage, copySysMessage);
        // 更新消息模板
        return this.updateById(copySysMessage);
    }

    /**
     * 删除消息推送模板
     */
    @Override
    public Boolean deleteMessageTemplate(Long id) {
        // 获取消息模板，验证是否存在以及是否是当前用户创建的
        SysMessage existMessage = this.getById(id);
        if (existMessage == null) {
            return false;
        }
        // 验证是否是创建者本人
        if (!existMessage.getSenderId().equals(LoginHelper.getUserId())) {
            return false;
        }
        // 删除消息模板
        return this.removeById(id);
    }

    /**
     * 查询消息模板类型 可以查询到系统消息模板加上自己创建的消息模板
     * @return
     */
    @Override
    public List<MessageType> queryMessageType() {
        return messageTypeService.list(new LambdaQueryWrapper<MessageType>()
                        .eq(MessageType::getCreateBy,LoginHelper.getUserId())
                        .or(e->e.eq(MessageType::getCreateBy,1001L)));
    }

    /**
     * 查询消息模板所有内容，所有用户均可以查看
     */
    @Override
    public List<SysMessage> queryAllMessageTemplateList(Integer type) {
        return this.list(new LambdaQueryWrapper<SysMessage>().eq(SysMessage::getType, type));
    }

    /**
     * 查询草稿箱内容
     */
    @Override
    public List<SysMessage> queryDraftMessageTemplate() {
        return  this.list(new LambdaQueryWrapper<SysMessage>()
                        .eq(SysMessage::getSenderId, LoginHelper.getUserId())
                        .eq(SysMessage::getType,3)
                        .eq(SysMessage::getPushStatus, 0));
    }
}




