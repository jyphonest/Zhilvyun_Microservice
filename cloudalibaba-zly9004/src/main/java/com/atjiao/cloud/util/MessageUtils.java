package com.atjiao.cloud.util;

import com.alibaba.fastjson2.JSON;
import com.atjiao.cloud.domain.chatPo.ResultMessage;

/**
 * @author 焦叶鹏
 * * @data 2025/9/9 21:28
 * @description: 消息获取工具类
 **/
public class MessageUtils {

    /**
     * @param isSystemMessage 是否是系统消息，只有广播消息才是系统消息，如果是私聊消息的话，就不是系统消息
     * @param fromName        给谁发消息，如果是系统消息的话，这个参数不需要指定
     * @param message         消息的具体内容
     * @return String
     */
    public static String getMessage(boolean isSystemMessage, String fromName, Object message) {
        ResultMessage resultMessage = new ResultMessage();

        resultMessage.setSystem(isSystemMessage);
        resultMessage.setMessage(message);
        if (fromName != null) {
            resultMessage.setFromName(fromName);
        }

        return JSON.toJSONString(resultMessage);
    }

}