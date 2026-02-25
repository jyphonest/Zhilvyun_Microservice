package com.atjiao.cloud.GlobalException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.resp.ReturnCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * @data 2025/7/10 23:07
 * @description: 全局异常处理 Sa-Token，提供统一的异常响应格式
 **/
@RestControllerAdvice
public class GlobalException {

    /**
     * 拦截：未登录异常
     * 包括：token不存在、token已过期、token无效等
     */
    @ExceptionHandler(NotLoginException.class)
    public ResultData<Object> handlerException(NotLoginException e) {
        // 打印堆栈，以供调试
        e.printStackTrace();

        // 构建详细的错误信息
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "NOT_LOGIN");
        errorDetail.put("loginType", e.getLoginType());
        errorDetail.put("detail", getNotLoginDetail(e.getLoginType()));
        errorDetail.put("suggestion", "请重新登录获取有效token");
        errorDetail.put("exceptionMessage", e.getMessage());

        // 根据登录类型选择对应的状态码
        ReturnCodeEnum returnCode = getReturnCodeByLoginType(e.getLoginType());
        
        return ResultData.fail(returnCode.getCode(), returnCode.getMessage()).setData(errorDetail);
    }

    /**
     * 拦截：缺少权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResultData<Object> handlerException(NotPermissionException e) {
        e.printStackTrace();
        
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "NO_PERMISSION");
        errorDetail.put("requiredPermission", e.getPermission());
        errorDetail.put("detail", "当前用户缺少执行此操作的权限");
        errorDetail.put("suggestion", "请联系管理员分配相应权限");
        errorDetail.put("exceptionMessage", e.getMessage());

        return ResultData.fail(ReturnCodeEnum.PERMISSION_DENIED.getCode(), 
                             ReturnCodeEnum.PERMISSION_DENIED.getMessage()).setData(errorDetail);
    }

    /**
     * 拦截：缺少角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResultData<Object> handlerException(NotRoleException e) {
        e.printStackTrace();
        
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "NO_ROLE");
        errorDetail.put("requiredRole", e.getRole());
        errorDetail.put("detail", "当前用户缺少执行此操作的角色");
        errorDetail.put("suggestion", "请联系管理员分配相应角色");
        errorDetail.put("exceptionMessage", e.getMessage());

        return ResultData.fail(ReturnCodeEnum.ROLE_DENIED.getCode(), 
                             ReturnCodeEnum.ROLE_DENIED.getMessage()).setData(errorDetail);
    }

    /**
     * 拦截：二级认证校验失败异常
     */
    @ExceptionHandler(NotSafeException.class)
    public ResultData<Object> handlerException(NotSafeException e) {
        e.printStackTrace();
        
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "NOT_SAFE");
        errorDetail.put("service", e.getService());
        errorDetail.put("detail", "当前操作需要额外的安全验证");
        errorDetail.put("suggestion", "请完成二级认证后重试");
        errorDetail.put("exceptionMessage", e.getMessage());

        return ResultData.fail(ReturnCodeEnum.SECONDARY_AUTH_REQUIRED.getCode(), 
                             ReturnCodeEnum.SECONDARY_AUTH_REQUIRED.getMessage()).setData(errorDetail);
    }

    /**
     * 拦截：服务封禁异常
     */
    @ExceptionHandler(DisableServiceException.class)
    public ResultData<Object> handlerException(DisableServiceException e) {
        e.printStackTrace();
        
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "SERVICE_DISABLED");
        errorDetail.put("service", e.getService());
        errorDetail.put("level", e.getLevel());
        errorDetail.put("disableTime", e.getDisableTime());
        errorDetail.put("detail", "当前账号因违规操作被临时封禁");
        errorDetail.put("suggestion", "请等待解封或联系客服处理");
        errorDetail.put("exceptionMessage", e.getMessage());

        return ResultData.fail(ReturnCodeEnum.ACCOUNT_DISABLED.getCode(), 
                             ReturnCodeEnum.ACCOUNT_DISABLED.getMessage()).setData(errorDetail);
    }

    /**
     * 拦截：其它所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResultData<Object> handlerException(Exception e) {
        e.printStackTrace();
        
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorType", "UNKNOWN_ERROR");
        errorDetail.put("exceptionClass", e.getClass().getSimpleName());
        errorDetail.put("detail", "系统发生未知错误");
        errorDetail.put("suggestion", "请稍后重试或联系技术支持");
        errorDetail.put("exceptionMessage", e.getMessage());

        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), 
                             ReturnCodeEnum.RC500.getMessage()).setData(errorDetail);
    }

    /**
     * 根据登录类型获取对应的状态码
     * @param loginType 登录类型
     * @return 对应的状态码枚举
     */
    private ReturnCodeEnum getReturnCodeByLoginType(String loginType) {
        if (loginType == null) {
            return ReturnCodeEnum.INVALID_TOKEN;
        }
        
        // 调试输出，查看实际的loginType值
        System.out.println("Debug - loginType: " + loginType);
        
        switch (loginType) {
            case "token-not-exist":
                return ReturnCodeEnum.TOKEN_NOT_EXIST;
            case "token-invalid":
                return ReturnCodeEnum.TOKEN_INVALID;
            case "token-timeout":
                return ReturnCodeEnum.TOKEN_EXPIRED;
            case "token-activity-timeout":
                return ReturnCodeEnum.TOKEN_ACTIVITY_TIMEOUT;
            case "token-kickout":
                return ReturnCodeEnum.TOKEN_KICKOUT;
            case "token-replace":
                return ReturnCodeEnum.TOKEN_REPLACED;
            case "login":
                // 处理特殊情况：login类型通常表示需要登录
                return ReturnCodeEnum.INVALID_TOKEN;
            default:
                // 对于未知的loginType，返回通用token无效状态码
                System.out.println("Debug - Unknown loginType: " + loginType + ", using INVALID_TOKEN");
                return ReturnCodeEnum.INVALID_TOKEN;
        }
    }

    /**
     * 根据登录类型获取详细的错误描述
     * @param loginType 登录类型
     * @return 错误描述
     */
    private String getNotLoginDetail(String loginType) {
        if (loginType == null) {
            return "未提供认证信息";
        }
        
        // 调试输出
        System.out.println("Debug - getNotLoginDetail called with loginType: " + loginType);
        
        switch (loginType) {
            case "token-not-exist":
                return "token不存在，请先登录";
            case "token-invalid":
                return "token格式无效";
            case "token-timeout":
                return "token已过期，请重新登录";
            case "token-activity-timeout":
                return "token因长时间未操作已失效";
            case "token-kickout":
                return "token已被踢下线";
            case "token-replace":
                return "token已被其他设备登录替换";
            case "login":
                return "需要登录才能访问此资源";
            default:
                return "认证失败：" + loginType;
        }
    }
}
