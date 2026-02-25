package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.UserMessage;
import com.atjiao.cloud.domain.vo.UserMessageVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.UserMessageService;
import com.atjiao.cloud.service.impl.UserMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/10/24 14:32
 * @description: 用户消息推送控制层
 **/
@RestController
@RequestMapping("/userMessage")
@RequiredArgsConstructor
public class UserMessageController {

    private  final UserMessageServiceImpl userMessageService;
    /**
     * 1.用户更新消息的阅读状态
     */
    @PostMapping("/updateMessageStatus/{id}")
    public void updateMessageStatus(@PathVariable Long id) {
        userMessageService.updateReadStatus(id);
    }

    /**
     * 2.用户查询自己的消息信息
     */
    @PostMapping("/queryMessageInfo")
    public ResultData<List<UserMessageVo>> queryMessageInfo() {
        return ResultData.success(userMessageService.queryMessageInfo());
    }

    /**
     * 3.用户删除自己的消息
     */
    @PostMapping("/deleteMessage/{id}")
    public void deleteMessage(@PathVariable Long id) {
        userMessageService.deleteMessage(id);
    }

    /**
     * 4.用户批量删除消息
     */
    @PostMapping("/deleteMessages")
    public void deleteMessages(@RequestBody List<Long> ids) {
         userMessageService.deleteMessages(ids);
    }
}
