package com.atjiao.cloud.helper;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 登录帮助工具类，用于获取当前登录用户数据库ID
 * 要求 loginId 必须为 Long 类型的 user.id
 */
public class LoginHelper {

    /**
     * 获取当前登录用户的数据库主键ID（user.id）
     * @return Long 类型用户ID
     */
    public static Long getUserId() {
        if (!StpUtil.isLogin()) {
            throw new RuntimeException("用户未登录");
        }

        try {
            return StpUtil.getLoginIdAsLong(); // 要求 loginId 是 Long 类型
        } catch (Exception e) {
            throw new RuntimeException("loginId 类型不合法，应为 Long 类型用户ID", e);
        }
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取当前登录用户的 Token
     */
    public static String getToken() {
        return StpUtil.getTokenValue();
    }
}
