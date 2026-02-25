package com.atjiao.cloud.config.SaTokenConfig;

import cn.dev33.satoken.stp.StpUtil;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.resp.ReturnCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * @data 2025/7/10 23:31
 * @description: Sa-Token工具类，提供token验证和操作的便捷方法
 */
public class SaTokenUtil {

    /**
     * 检查用户是否已登录
     * @return 登录状态检查结果
     */
    public static ResultData<Object> checkLoginStatus() {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("loginId", StpUtil.getLoginId());
            userInfo.put("loginType", StpUtil.getLoginType());
            userInfo.put("tokenInfo", StpUtil.getTokenInfo());
            
            return ResultData.success(userInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "检查登录状态失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录时返回null
     */
    public static Object getCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginId();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查用户是否具有指定权限
     * @param permission 权限标识
     * @return 权限检查结果
     */
    public static ResultData<Object> checkPermission(String permission) {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            if (StpUtil.hasPermission(permission)) {
                Map<String, Object> successInfo = new HashMap<>();
                successInfo.put("permission", permission);
                successInfo.put("detail", "权限验证通过");
                
                return ResultData.success(successInfo);
            } else {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("requiredPermission", permission);
                errorInfo.put("detail", "当前用户缺少执行此操作的权限");
                errorInfo.put("suggestion", "请联系管理员分配相应权限");
                
                return ResultData.fail(ReturnCodeEnum.PERMISSION_DENIED.getCode(), 
                                     ReturnCodeEnum.PERMISSION_DENIED.getMessage()).setData(errorInfo);
            }
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "权限检查失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 检查用户是否具有指定角色
     * @param role 角色标识
     * @return 角色检查结果
     */
    public static ResultData<Object> checkRole(String role) {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            if (StpUtil.hasRole(role)) {
                Map<String, Object> successInfo = new HashMap<>();
                successInfo.put("role", role);
                successInfo.put("detail", "角色验证通过");
                
                return ResultData.success(successInfo);
            } else {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("requiredRole", role);
                errorInfo.put("detail", "当前用户缺少执行此操作的角色");
                errorInfo.put("suggestion", "请联系管理员分配相应角色");
                
                return ResultData.fail(ReturnCodeEnum.ROLE_DENIED.getCode(), 
                                     ReturnCodeEnum.ROLE_DENIED.getMessage()).setData(errorInfo);
            }
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "角色检查失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 获取用户权限列表
     * @return 权限列表
     */
    public static ResultData<Object> getUserPermissions() {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            Map<String, Object> permissionInfo = new HashMap<>();
            permissionInfo.put("permissions", StpUtil.getPermissionList());
            permissionInfo.put("roles", StpUtil.getRoleList());
            
            return ResultData.success(permissionInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "获取权限信息失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 检查token是否即将过期
     * @param warningTime 提前警告时间（秒）
     * @return 检查结果
     */
    public static ResultData<Object> checkTokenExpiration(long warningTime) {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            long tokenTimeout = StpUtil.getTokenTimeout();
            if (tokenTimeout <= warningTime) {
                Map<String, Object> warningInfo = new HashMap<>();
                warningInfo.put("remainingTime", tokenTimeout);
                warningInfo.put("warning", "token即将过期，建议及时刷新");
                warningInfo.put("suggestion", "请及时刷新token或重新登录");
                
                return ResultData.fail(ReturnCodeEnum.TOKEN_EXPIRED.getCode(), 
                                     ReturnCodeEnum.TOKEN_EXPIRED.getMessage()).setData(warningInfo);
            }
            
            Map<String, Object> successInfo = new HashMap<>();
            successInfo.put("remainingTime", tokenTimeout);
            successInfo.put("detail", "token有效期正常");
            
            return ResultData.success(successInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "检查token过期时间失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 强制用户下线
     * @param loginId 用户ID
     * @return 操作结果
     */
    public static ResultData<Object> forceLogout(Object loginId) {
        try {
            StpUtil.kickout(loginId);
            
            Map<String, Object> successInfo = new HashMap<>();
            successInfo.put("loginId", loginId);
            successInfo.put("detail", "用户已被强制下线");
            
            return ResultData.success(successInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "强制下线失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }

    /**
     * 获取当前会话信息
     * @return 会话信息
     */
    public static ResultData<Object> getSessionInfo() {
        try {
            if (!StpUtil.isLogin()) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("detail", "用户未登录");
                errorInfo.put("suggestion", "请先登录系统");
                
                return ResultData.fail(ReturnCodeEnum.INVALID_TOKEN.getCode(), 
                                     ReturnCodeEnum.INVALID_TOKEN.getMessage()).setData(errorInfo);
            }
            
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("loginId", StpUtil.getLoginId());
            sessionInfo.put("loginType", StpUtil.getLoginType());
            sessionInfo.put("tokenInfo", StpUtil.getTokenInfo());
            sessionInfo.put("sessionInfo", StpUtil.getSession());
            
            return ResultData.success(sessionInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("detail", "获取会话信息失败");
            errorInfo.put("exceptionMessage", e.getMessage());
            errorInfo.put("suggestion", "请稍后重试");
            
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                                 ReturnCodeEnum.RC500.getMessage()).setData(errorInfo);
        }
    }
}
