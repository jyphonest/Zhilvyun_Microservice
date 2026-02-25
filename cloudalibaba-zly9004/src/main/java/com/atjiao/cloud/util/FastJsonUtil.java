package com.atjiao.cloud.util;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 焦叶鹏
 * * @data 2025/10/16 19:26
 * @description: 序列化工具类
 **/
@Slf4j
public class FastJsonUtil {

    /**
     * 安全地解析 JSON 字符串：
     * 1. 自动去除控制字符、BOM。
     * 2. 自动修复转义 JSON（例如 {\"key\":\"value\"}）。
     * 3. 自动捕获异常并打印错误日志。
     *
     * @param jsonStr 原始 JSON 字符串（可能带有非法字符或转义）
     * @return JSONObject，如果解析失败则返回空对象（非 null）
     */
    public static JSONObject parseSafe(String jsonStr) {
        if (jsonStr == null) {
            return new JSONObject();
        }

        try {
            // 1️⃣ 清洗控制字符、空字节、BOM
            String cleaned = jsonStr
                    .replaceAll("[\\x00-\\x1F\\x7F]", "")  // 控制字符
                    .replace("\uFEFF", "")                 // BOM
                    .trim();

            // 2️⃣ 修复转义 JSON（形如 {\"userId\":\"123\"}）
            if (cleaned.startsWith("{\\") || cleaned.contains("\\\"")) {
                cleaned = cleaned
                        .replaceAll("^\"|\"$", "")  // 去掉最外层引号
                        .replace("\\\"", "\"");     // 反转义
            }

            // 3️⃣ 解析为 JSONObject
            return JSON.parseObject(cleaned);
        } catch (Exception e) {
            log.error("❌ FastJsonUtil 解析失败: {}，原始内容: {}", e.getMessage(), jsonStr);
            return new JSONObject(); // 返回空对象防止空指针
        }
    }

    /**
     * 判断字符串是否是有效 JSON。
     *
     * @param jsonStr 待检测字符串
     * @return true=是JSON，false=不是
     */
    public static boolean isValidJson(String jsonStr) {
        try {
            JSON.parse(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

