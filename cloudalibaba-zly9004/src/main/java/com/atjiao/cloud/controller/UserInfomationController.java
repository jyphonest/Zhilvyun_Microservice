package com.atjiao.cloud.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.dto.userAuthorityDTO;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.UserInformationService;
import com.atjiao.cloud.util.SmsUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/5 20:22
 * @description: TODO
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/userInformation")
@Slf4j
public class UserInfomationController {

    private final UserInformationService userInformationService;

    @PostMapping("/test")
    public List<UserInformation> test(@RequestBody UserInformation userInformation) {
            return userInformationService.getUserByAccountNumber(userInformation);
    }

    /**
     * 前台用户登录接口
     * 账号密码验证通过执行-->StpUtil.login()方法，该方法会将账号id存入session中，
     * 后续可以直接通过StpUtil.getLoginId()获取到账号id
     * 前端通过session中的token请求页面数据
     */
    @PostMapping("/doLogin")
    public ResultData<Object> doLogin(@Valid @RequestBody UserInformation userInformation, HttpSession session) {
        return ResultData.success(userInformationService.login(userInformation,session));
    }

    /**
     * 后台用户登录接口
     */
    @PostMapping("/adminLogin")
    public ResultData<Object> adminLogin(@Valid @RequestBody UserInformation userInformation,HttpSession httpSession) {
        return ResultData.success(userInformationService.adminLogin(userInformation,httpSession));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Boolean register(@Valid @RequestBody UserInformation userInformation) {
       return userInformationService.register(userInformation);
    }


    /**
     * 退出登录
     */
    @RequestMapping("/exitLogin")
    public SaResult exit() {
        StpUtil.logout();
        return SaResult.ok("退出登录成功");
    }

    /**
     * 获取当前登陆用户账号名
     */
    @GetMapping("/getLoginName")
    public ResultData<String> getLoginName(HttpSession httpSession) {
        log.info("测试当前currentUser对象的值：{}", httpSession.getAttribute("currentUser"));
        return ResultData.success((String) httpSession.getAttribute("currentUser"));
    }

    /**
     * 后台获取当前登陆的用户名
     * @return
     */
    @GetMapping("/adminGetLoginName")
    public ResultData<String> adminGetLoginName(HttpSession httpSession) {
        log.info("测试当前currentUser对象的值：{}", httpSession.getAttribute("currentUser"));
        return ResultData.success((String) httpSession.getAttribute("currentUser"));
    }

    @GetMapping("/getUser")
    public SaResult getUser() {
        // 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
        Object loginId = StpUtil.getLoginId();

        StpUtil.getLoginIdAsString();    // 获取当前会话账号id, 并转化为`String`类型
        StpUtil.getLoginIdAsInt();       // 获取当前会话账号id, 并转化为`int`类型
        StpUtil.getLoginIdAsLong();      // 获取当前会话账号id, 并转化为`long`类型

        // 获取当前会话账号id, 如果未登录，则返回 null
        StpUtil.getLoginIdDefaultNull();

        // 获取当前会话的 token 值
        String tokenValue = StpUtil.getTokenValue();

// 获取当前`StpLogic`的 token 名称
        StpUtil.getTokenName();

// 获取指定 token 对应的账号id，如果未登录，则返回 null
//         StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前会话剩余有效期（单位：s，返回-1代表永久有效）
        long tokenTimeout = StpUtil.getTokenTimeout();

// 获取当前会话的 token 信息参数
        StpUtil.getTokenInfo();

        return SaResult.ok("当前登录用户ID：" + loginId
        + "\n当前会话的 token 值：" + tokenValue
        + "\n当前会话剩余有效期（单位：s，返回-1代表永久有效）：" + tokenTimeout);
    }

    /**
     * 踢人下线
     */
    @PostMapping("/kickout/{userId}")
    public SaResult kickout(@PathVariable("userId") Long userId) {
        StpUtil.kickout(userId) ;
        log.info("踢人下线成功，tokenValue");
        return SaResult.ok();
    }

    /**
     * 权限校验
     */
    @PostMapping("/verification")
    public SaResult checkRole() {
        // 获取：当前账号所拥有的权限集合
        StpUtil.getPermissionList();
        log.info("当前账号所拥有的权限集合：{}", StpUtil.getPermissionList());
// 判断：当前账号是否含有指定权限, 返回 true 或 false
        StpUtil.hasPermission("user.add");
        log.info("当前账号是否含有指定权限：{}", StpUtil.hasPermission("user.add"));

        /**
         * 角色校验
         */
        // 获取：当前账号所拥有的角色集合
        StpUtil.getRoleList();
        log.info("当前账号所拥有的角色集合：{}", StpUtil.getRoleList());

        StpUtil.hasRole("super-admin");
        log.info("当前账号是否含有指定角色：{}", StpUtil.hasRole("super-admin"));

        return SaResult.ok();
    }


    /**
     * 后台页面条件搜索用户信息
     */
    @PostMapping("/adminSearchUser")
    public ResultData<IPage<UserInformation>> adminSearchUser(@RequestBody UserInformation userInformation, PageQuery pageQuery) {
        return ResultData.success(userInformationService.adminSearchUser(userInformation,pageQuery));
    }

    /**
     * 后台页面修改用户信息
     */
    @PostMapping("/adminUpdateUser")
    public ResultData<Object> adminUpdateUser(@RequestBody UserInformation userInformation) {
        return ResultData.success(userInformationService.updateById(userInformation));
    }

    /**
     * 后台管理员为用户分配权限，需要输入密码确认
     */
    @PostMapping("/adminAssignRole")
    public ResultData<Object> adminAssignRole(@RequestBody userAuthorityDTO userInformation) {
        return ResultData.success(userInformationService.assignRole(userInformation));
    }

    /**
     * 用户获取个人信息
     */
    @GetMapping("/getUserInformation")
    public ResultData<UserInformation> getUserInformation() {
        return ResultData.success(userInformationService.getById(LoginHelper.getUserId()));
    }

    /**
     * 用户修改个人信息
     */
    @PostMapping("/updateUserInformation")
    public ResultData<Object> updateUserInformation(@RequestBody UserInformation userInformation) {
        userInformation.setId(LoginHelper.getUserId());
        return ResultData.success(userInformationService.updateById(userInformation));
    }

    /**
     * 用户注册时发送验证码
     */
    @PostMapping("/getVerificationCode")
    @SaIgnore
    public ResultData<Object> getVerificationCode(@RequestParam String phone) {
        boolean result = userInformationService.sendVerificationCode(phone);
        if (result) {
            return ResultData.success("验证码发送成功");
        } else {
            return ResultData.fail("500","验证码发送失败");
        }
    }


}
