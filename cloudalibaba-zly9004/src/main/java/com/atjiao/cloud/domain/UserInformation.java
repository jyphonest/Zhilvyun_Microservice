package com.atjiao.cloud.domain;


import com.baomidou.mybatisplus.annotation.*;
import java.util.Date;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 
 * @TableName user_information
 */
@Data
public class UserInformation extends BaseEntity{
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String accountNumber;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 当前余额
     */
    private Long currentBalance;

    /**
     * 累计充值
     */
    private Long cumulativeRecharge;

    /**
     * 是否VIP
     */
    private Integer vipOrNot;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 权限
     */
    private String authority;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 积分
     */
    private Long integral;

    /**
     * 真实姓名
     */
    private String fullName;

    private Integer status;

    private Integer sex;
}