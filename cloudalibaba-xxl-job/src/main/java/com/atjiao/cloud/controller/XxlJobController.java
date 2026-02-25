package com.atjiao.cloud.controller;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 焦叶鹏
 * * @data 2025/7/30 10:10
 * @description: 分布式任务调度平台XXL-JOB控制器
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class XxlJobController {

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("rltDemoJobHandler")
    public void rltDemoJobHandler() throws Exception {
        XxlJobHelper.log("My Name Is Jiao Ye Peng ,Hello World.");
    }
}

