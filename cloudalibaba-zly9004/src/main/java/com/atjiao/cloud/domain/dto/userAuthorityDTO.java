package com.atjiao.cloud.domain.dto;

import lombok.Data;

/**
 * @author 焦叶鹏
 * * @data 2025/11/5 21:10
 * @description: 用户分配角色权限接收实体类
 **/
@Data
public class userAuthorityDTO {

    private  Long userId;

    private  String authority;


    private String password;

}
