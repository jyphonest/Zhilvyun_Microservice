package com.atjiao.cloud.config.SaTokenConfig;

import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 焦叶鹏
 * @data 2025/7/10 23:31
 * @description: 自定义Sa-Token拦截器，用于验证token有效性，异常交给全局异常处理器处理
 */
public class CustomSaTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 检查是否已登录（如果未登录会抛出NotLoginException）
            StpUtil.checkLogin();

            // 检查账号是否被封禁
            // if (StpUtil.isDisable()) {
            //     throw new DisableServiceException("当前账号已被封禁");
            // }

            // 检查是否通过二级认证（如果需要的话）
            // if (!StpUtil.isSafe()) {
            //     throw new NotSafeException("需要二级认证");
            // }
            
            return true;
            
        } catch (NotLoginException e) {
            // 直接抛出异常，让全局异常处理器处理
            throw e;
        } catch (DisableServiceException e) {
            // 直接抛出异常，让全局异常处理器处理
            throw e;
        } catch (NotSafeException e) {
            // 直接抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            // 其他异常也交给全局异常处理器
            throw e;
        }
    }
}
