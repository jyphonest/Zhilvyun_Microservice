package com.atjiao.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.service.MessageTypeService;
import com.atjiao.cloud.mapper.MessageTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author 86182
* @description 针对表【message_type(消息类型表)】的数据库操作Service实现
* @createDate 2025-10-16 18:56:43
*/
@Service
public class MessageTypeServiceImpl extends ServiceImpl<MessageTypeMapper, MessageType>
    implements MessageTypeService{

}




