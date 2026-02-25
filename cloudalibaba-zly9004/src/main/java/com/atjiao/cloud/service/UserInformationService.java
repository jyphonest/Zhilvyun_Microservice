package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.dto.userAuthorityDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
* @author 焦叶鹏
* @description 针对表【user_information】的数据库操作Service
* @createDate 2025-07-05 20:17:43
*/
public interface UserInformationService extends IService<UserInformation> {

    List<UserInformation> getUserByAccountNumber(UserInformation userInformation);
    /**
     * 用户登录
     */
    Map<String, Object> login(UserInformation userInformation, HttpSession session);
    /**
     * 用户注册
     */
    boolean register(UserInformation userInformation);
    /**
     * 后台管理员登录
     */
    Map<String, Object> adminLogin(UserInformation userInformation,HttpSession httpSession);


    IPage<UserInformation> adminSearchUser(UserInformation search, PageQuery pageQuery);

    boolean assignRole(userAuthorityDTO userInformation);

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return 发送结果
     */
    boolean sendVerificationCode(String phone);
}
