package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.MessageTemplate;
import com.atjiao.cloud.domain.MessageType;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.dto.MessageTemplateDTO;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.MessageTemplateService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/11/19 15:58
 * @description: 消息推送模板控制层
 **/
@RestController
@RequestMapping("/messageTemplate")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;


    /**
     * 查询用户自己是否有创建消息模板
     */
    @GetMapping("/queryMessageTemplate")
    public ResultData<Boolean> queryMessageTemplate() {
        return ResultData.success(messageTemplateService.queryMessageTemplate());
    }

    /**
     * 查询用户创建的消息模板列表
     */
    @GetMapping("/list")
    public ResultData<List<MessageTemplate>> queryMessageTemplateList() {
        List<MessageTemplate> messageList = messageTemplateService.queryMessageTemplateList();
        return ResultData.success(messageList);
    }

    /**
     * 创建消息推送模板
     */
    @PostMapping("/create")
    public ResultData<Boolean> createMessageTemplate(@RequestBody MessageTemplateDTO messageTemplateDTO) {
        Boolean result = messageTemplateService.createMessageTemplate(messageTemplateDTO);
        return result ? ResultData.success(true) : ResultData.fail("201","创建消息模板失败");
    }

    /**
     * 更新消息推送模板
     */
    @PostMapping("/update")
    public ResultData<Boolean> updateMessageTemplate(@RequestBody MessageTemplateDTO messageTemplateDTO) {
        Boolean result = messageTemplateService.updateMessageTemplate(messageTemplateDTO);
        return result ? ResultData.success(true) : ResultData.fail("201","更新消息模板失败，请检查模板是否存在或您是否有权限");
    }

    /**
     * 删除消息推送模板
     */
    @PostMapping("/delete/{id}")
    public ResultData<Boolean> deleteMessageTemplate(@PathVariable Long id) {
        Boolean result = messageTemplateService.deleteMessageTemplate(id);
        return result ? ResultData.success(true) : ResultData.fail("201","删除消息模板失败，请检查模板是否存在或您是否有权限");
    }

    /**
     * 查询消息模板类型
     */
    @GetMapping("/queryMessageType")
    public ResultData<List<MessageType>> queryMessageType() {
        return ResultData.success(messageTemplateService.queryMessageType());
    }

    /**
     * 查询消息模板所有内容，所有用户均可以查看
     * type: "0"为正式模板内容，"1"为草稿模板内容  2为用户创建的模板内容
     */
    @GetMapping("/queryAllMessageTemplateList/{type}")
    public ResultData<List<MessageTemplate>> queryAllMessageTemplateList(@PathVariable String type) {
        return ResultData.success(messageTemplateService.queryAllMessageTemplateList(type));
    }

    /**
     * 用户查询消息模板的草稿内容
     */
    @PostMapping("/queryDraftMessageTemplate")
    public ResultData<IPage<MessageTemplate>> queryDraftMessageTemplate(PageQuery pageQuery) {
        return ResultData.success(messageTemplateService.queryDraftMessageTemplate(pageQuery));
    }
}
