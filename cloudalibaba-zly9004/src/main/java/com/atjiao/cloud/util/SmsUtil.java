package com.atjiao.cloud.util;

import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.atjiao.cloud.config.AliyunSmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author 焦叶鹏
 * @since 2025/1/4
 * @description: 阿里云短信服务工具类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsUtil {

    private final AliyunSmsConfig aliyunSmsConfig;

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @return 验证码
     */
    public String sendSmsVerificationCode(String phoneNumber) throws Exception {

        // 1. 生成 6 位验证码
        String verificationCode = generateVerificationCode();

        // 2. 创建号码认证 Client
        com.aliyun.dypnsapi20170525.Client client = createClient();

        // 3. 构建【号码认证服务】短信验证码请求
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phoneNumber)
                .setSignName("速通互联验证码")   // 必须是你赠送/审核通过的签名
                .setTemplateCode("100001")      // 号码认证服务里的模板
                .setTemplateParam("{\"code\":\"" + verificationCode + "\"}");

        try {
            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponseBody body = response.getBody();

            if ("OK".equals(body.getCode())) {
                log.info("短信验证码发送成功，手机号：{}", phoneNumber);
                return verificationCode;
            } else {
                log.error("短信验证码发送失败，错误码：{}，错误信息：{}",
                        body.getCode(), body.getMessage());
                throw new RuntimeException("短信验证码发送失败：" + body.getMessage());
            }
        } catch (TeaException e) {
            log.error("短信验证码发送异常", e);
            throw new RuntimeException("短信验证码发送异常：" + e.getMessage());
        }
    }



    /**
     * 创建阿里云短信服务客户端
     * @return 客户端实例
     */
    private com.aliyun.dypnsapi20170525.Client createClient() throws Exception {

        Config config = new Config()
                .setAccessKeyId(aliyunSmsConfig.getAccessKeyId())
                .setAccessKeySecret(aliyunSmsConfig.getAccessKeySecret());

        // ⚠️ 号码认证服务固定 Endpoint
        config.endpoint = "dypnsapi.aliyuncs.com";

        return new com.aliyun.dypnsapi20170525.Client(config);
    }


    /**
     * 生成6位随机验证码
     * @return 验证码字符串
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // 生成100000-999999之间的随机数
        return String.valueOf(code);
    }
}