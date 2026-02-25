package com.atjiao.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.MessagePushLog;
import com.atjiao.cloud.service.MessagePushLogService;
import com.atjiao.cloud.mapper.MessagePushLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 86182
* @description 针对表【message_push_log(消息推送日志表)】的数据库操作Service实现
* @createDate 2025-10-16 18:56:40
*/
@Service
public class MessagePushLogServiceImpl extends ServiceImpl<MessagePushLogMapper, MessagePushLog>
    implements MessagePushLogService{

}




