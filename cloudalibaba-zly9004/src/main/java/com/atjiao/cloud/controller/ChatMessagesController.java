package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.ChatMessages;
import com.atjiao.cloud.domain.dto.ChatMessagesDTO;
import com.atjiao.cloud.domain.vo.ChatMessagesVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.ChatMessagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 焦叶鹏
 * @data 2025/9/12 23:44
 * @description: 聊天消息控制器
 **/
@RestController
@RequestMapping("/chatMessages")
@RequiredArgsConstructor
@Slf4j
public class ChatMessagesController {
    
    private final ChatMessagesService chatMessagesService;
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResultData<String> sendMessage(@RequestBody ChatMessagesDTO chatMessagesDTO) {
        chatMessagesService.sendMessage(chatMessagesDTO);
        return ResultData.success("消息发送成功");
    }
    
    /**
     * 获取会话消息列表
     */
    @GetMapping("/list/{sessionId}")
    public ResultData<List<ChatMessagesVo>> getMessageList(@PathVariable Long sessionId) {
        List<ChatMessagesVo> messages = chatMessagesService.getMessageList(sessionId);
        return ResultData.success(messages);
    }
    
    /**
     * 设置消息已读
     */
    @PostMapping("/read/{messageId}")
    public ResultData<String> setMessageRead(@PathVariable Long messageId, @RequestParam Long currentUserId) {
        chatMessagesService.setMessageRead(messageId, currentUserId);
        return ResultData.success("消息已标记为已读");
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/revoke/{messageId}")
    public ResultData<String> revokeMessage(@PathVariable Long messageId, @RequestParam Long currentUserId) {
        chatMessagesService.revokeMessage(messageId, currentUserId);
        return ResultData.success("消息撤回成功");
    }
    
    /**
     * 获取单条消息详情
     */
    @GetMapping("/{messageId}")
    public ResultData<ChatMessages> getMessage(@PathVariable Long messageId) {
        ChatMessages message = chatMessagesService.getMessage(messageId);
        if (message == null) {
            return ResultData.fail("404", "消息不存在");
        }
        return ResultData.success(message);
    }
}
