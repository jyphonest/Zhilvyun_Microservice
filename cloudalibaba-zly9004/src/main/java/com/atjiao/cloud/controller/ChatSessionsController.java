package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.ChatSessions;
import com.atjiao.cloud.domain.vo.ChatSessionsVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.ChatSessionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 焦叶鹏
 * @data 2025/9/12 23:45
 * @description: 聊天会话控制器
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/chatSessions")
@Slf4j
public class ChatSessionsController {
    
    private final ChatSessionsService chatSessionsService;
    
    /**
     * 创建聊天会话
     */
    @PostMapping("/create")
    public ResultData<ChatSessions> createSession(@RequestParam Long userId, 
                                                 @RequestParam Long staffId) {
        try {
            ChatSessions session = chatSessionsService.createSession(userId, staffId);
            return ResultData.success(session);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return ResultData.fail("500", "创建会话失败：" + e.getMessage());
        }
    }
    
    /**
     * 分配客服
     */
    @PutMapping("/assign/{sessionId}")
    public ResultData<String> assignStaff(@PathVariable Long sessionId, 
                                        @RequestParam Long staffId) {
        try {
            chatSessionsService.assignStaff(sessionId, staffId);
            return ResultData.success("客服分配成功");
        } catch (Exception e) {
            log.error("分配客服失败", e);
            return ResultData.fail("500", "分配客服失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的会话列表
     */
    @GetMapping("/list/{userId}")
    public ResultData<List<ChatSessions>> getSessionList(@PathVariable Long userId) {
        try {
            List<ChatSessions> sessions = chatSessionsService.getSessionList(userId);
            return ResultData.success(sessions);
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return ResultData.fail("500", "获取会话列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 结束会话
     */
    @PutMapping("/end/{sessionId}")
    public ResultData<String> endSession(@PathVariable Long sessionId) {
        try {
            chatSessionsService.endSession(sessionId);
            return ResultData.success("会话已结束");
        } catch (Exception e) {
            log.error("结束会话失败", e);
            return ResultData.fail("500", "结束会话失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/details/{sessionId}")
    public ResultData<ChatSessionsVo> getSessionDetails(@PathVariable Long sessionId) {
        try {
            ChatSessionsVo sessionDetails = chatSessionsService.getSessionDetails(sessionId);
            if (sessionDetails == null) {
                return ResultData.fail("404", "会话不存在");
            }
            return ResultData.success(sessionDetails);
        } catch (Exception e) {
            log.error("获取会话详情失败", e);
            return ResultData.fail("500", "获取会话详情失败：" + e.getMessage());
        }
    }
}
