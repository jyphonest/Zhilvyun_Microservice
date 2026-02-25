package com.atjiao.cloud.service;

import cn.dev33.satoken.context.second.SaTokenSecondContext;
import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.SysMessage;
import com.atjiao.cloud.domain.dto.SysMessageDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86182
* @description 针对表【message(消息推送主表)】的数据库操作Service
* @createDate 2025-10-16 18:56:04
*/
public interface MessageService extends IService<SysMessage> {

    Boolean queryMessageTemplate();

    List<SysMessage> queryMessageTemplateList();

    Boolean createMessageTemplate(SysMessageDto sysMessage);

    Boolean updateMessageTemplate(SysMessageDto sysMessage);

    Boolean deleteMessageTemplate(Long id);

    List<MessageType>queryMessageType();

    List<SysMessage>queryAllMessageTemplateList(Integer type);

    List<SysMessage>queryDraftMessageTemplate();
}
