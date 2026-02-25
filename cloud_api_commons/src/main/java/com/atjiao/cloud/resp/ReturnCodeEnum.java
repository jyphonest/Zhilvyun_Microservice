package com.atjiao.cloud.resp;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author jyp
 * @data 2025/2/28
 **/

@Getter
public enum ReturnCodeEnum {

    /**操作失败**/
    RC999("999","操作XXX失败"),
    RC801("801","插入数据失败"),
    /**操作成功**/
    RC200("200","success"),
    /**服务降级**/
    RC201("201","服务开启降级保护,请稍后再试!"),
    /**热点参数限流**/
    RC202("202","热点参数限流,请稍后再试!"),
    /**系统规则不满足**/
    RC203("203","系统规则不满足要求,请稍后再试!"),
    /**授权规则不通过**/
    RC204("204","授权规则不通过,请稍后再试!"),
    /**access_denied**/
    RC403("403","无访问权限,请联系管理员授予权限"),
    /**access_denied**/
    RC401("401","匿名用户访问无权限资源时的异常"),
    RC404("404","404页面找不到的异常"),
    /**服务异常**/
    RC500("500","系统异常，请稍后重试"),
    RC375("375","数学运算异常，请稍后重试"),

    INVALID_TOKEN("2001","访问令牌不合法"),
    ACCESS_DENIED("2003","没有权限访问该资源"),
    CLIENT_AUTHENTICATION_FAILED("1001","客户端认证失败"),
    USERNAME_OR_PASSWORD_ERROR("1002","用户名或密码错误"),
    BUSINESS_ERROR("1004","业务逻辑异常"),
    UNSUPPORTED_GRANT_TYPE("1003", "不支持的认证模式"),

    // Sa-Token 相关状态码
    /** 认证失败相关 **/
    TOKEN_NOT_EXIST("3001", "访问令牌不存在"),
    TOKEN_INVALID("3002", "访问令牌格式无效"),
    TOKEN_EXPIRED("3003", "访问令牌已过期"),
    TOKEN_ACTIVITY_TIMEOUT("3004", "访问令牌因长时间未操作已失效"),
    TOKEN_KICKOUT("3005", "访问令牌已被踢下线"),
    TOKEN_REPLACED("3006", "访问令牌已被其他设备登录替换"),
    
    /** 权限相关 **/
    PERMISSION_DENIED("3007", "缺少必要权限"),
    ROLE_DENIED("3008", "缺少必要角色"),
    SECONDARY_AUTH_REQUIRED("3009", "需要二级认证"),
    
    /** 账号状态相关 **/
    ACCOUNT_DISABLED("3010", "账号已被封禁"),
    ACCOUNT_LOCKED("3011", "账号已被锁定"),
    
    /** 会话相关 **/
    SESSION_EXPIRED("3012", "会话已过期"),
    SESSION_INVALID("3013", "会话无效");

    private final String code;
    private final String message;
    ReturnCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    //遍历枚举方式一（基础版）
    public static ReturnCodeEnum getReturnCodeEnum(String code) {
        for (ReturnCodeEnum element : ReturnCodeEnum.values()) {
            if(element.getCode().equalsIgnoreCase(code))
            {
                return element;
            }
        }
        return null;
    }

    //遍历枚举方式二（进阶版）
    public static ReturnCodeEnum getReturnCodeEnumV2(String code) {
        return Arrays.stream(ReturnCodeEnum.values()).filter
                (item -> item.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

    public static void main(String[] args) {
        System.out.println(getReturnCodeEnumV2("200"));
        System.out.println(getReturnCodeEnumV2("200").getCode());
        System.out.println(getReturnCodeEnumV2("200").getMessage());

    }
}
