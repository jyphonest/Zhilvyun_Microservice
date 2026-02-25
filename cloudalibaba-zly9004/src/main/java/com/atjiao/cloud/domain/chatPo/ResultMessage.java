package com.atjiao.cloud.domain.chatPo;

import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/9/9 21:36
 * @description: TODO
 **/
@Data
public class ResultMessage {

    private boolean isSystem;
    private String fromName;
    private Object message;// 如果是系统消息是数组
}
