package com.atjiao.cloud.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.atjiao.cloud.domain.PageQuery;
import com.atjiao.cloud.domain.UserInformation;
import com.atjiao.cloud.domain.dto.userAuthorityDTO;
import com.atjiao.cloud.helper.LoginHelper;
import com.atjiao.cloud.mapper.UserInformationMapper;
import com.atjiao.cloud.service.UserInformationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.atjiao.cloud.util.SmsUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.IIOParam;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 焦叶鹏
* @description 针对表【user_information】的数据库操作Service实现
* @createDate 2025-07-05 20:17:43
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserInformationServiceImpl extends ServiceImpl<UserInformationMapper, UserInformation>
    implements UserInformationService {

    private final UserInformationMapper userInformationMapper;
    private final SmsUtil smsUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<UserInformation> getUserByAccountNumber(UserInformation userInformation) {
        return userInformationMapper.selectList(
                new LambdaQueryWrapper<UserInformation>()
                        .eq(UserInformation::getAccountNumber, userInformation.getAccountNumber())
        );
    }

    /**
     * 前台用户登录
     *
     * @param userInformation
     * @return
     */
    @Override
    public Map<String, Object> login(UserInformation userInformation, HttpSession httpSession) {
        String accountNumber = userInformation.getAccountNumber();
        UserInformation user = userInformationMapper.selectOne(
                new LambdaQueryWrapper<UserInformation>()
                        .eq(UserInformation::getAccountNumber, accountNumber)
        );
        if (user != null && BCrypt.checkpw(userInformation.getPassword(), user.getPassword())) {
            StpUtil.login(user.getId());
            StpUtil.getSession().set("userInfo", user);
            httpSession.setAttribute("currentUser", user.getAccountNumber());
            String token = StpUtil.getTokenValue();
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("status", "登录成功");
            log.info("登录成功,当前登录用户id为{}", LoginHelper.getUserId());
            return result;
        } else {
            throw new RuntimeException("账号或密码错误");
        }
    }

    /**
     * 后台管理员登录
     */
    @Override
    public Map<String, Object> adminLogin(UserInformation userInformation,HttpSession httpSession) {
        String accountNumber = userInformation.getAccountNumber();
        UserInformation user = userInformationMapper.selectOne(
                new LambdaQueryWrapper<UserInformation>()
                        .eq(UserInformation::getAccountNumber, accountNumber)
        );
        if (user != null && BCrypt.checkpw(userInformation.getPassword(), user.getPassword())) {
            StpUtil.login(user.getId());
            StpUtil.getSession().set("userInfo", user);
            httpSession.setAttribute("currentUser", user.getAccountNumber());
            if(!StpUtil.hasRole("admin")){
                throw new RuntimeException("账号无权限");
            }
            String token = StpUtil.getTokenValue();
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("status", "登录成功");
            return result;
        } else {
            throw new RuntimeException("账号或密码错误");
        }
    }


    @Override
    public IPage<UserInformation> adminSearchUser(UserInformation search, PageQuery pageQuery) {
        LambdaQueryWrapper<UserInformation> query = new LambdaQueryWrapper<>();
        if(search != null){
            if(search.getFullName() != null){
                query.like(UserInformation::getFullName, search.getFullName());
            }
            if(search.getEmail()!=null){
                query.like(UserInformation::getEmail, search.getEmail());
            }
            if(search.getAuthority()!=null){
                query.like(UserInformation::getAuthority, search.getAuthority());
            }
            if(search.getStatus()!=null){
                query.eq(UserInformation::getStatus, search.getStatus());
            }
        }
        
        // 设置默认分页参数
        int pageNum = (pageQuery.getPageNum() == null || pageQuery.getPageNum() <= 0) ? PageQuery.DEFAULT_PAGE_NUM : pageQuery.getPageNum();
        int pageSize = (pageQuery.getPageSize() == null || pageQuery.getPageSize() <= 0) ? PageQuery.DEFAULT_PAGE_SIZE : pageQuery.getPageSize();
        
        // 创建分页对象
        IPage<UserInformation> page = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        return userInformationMapper.selectPage(page, query);
    }

    @Override
    public boolean assignRole(userAuthorityDTO userInformation) {

        Long userId = userInformation.getUserId();
        // 1. 入参非空校验（防止空指针）
        if (userInformation == null || userInformation.getUserId() == null
                || StringUtil.isNullOrEmpty(userInformation.getPassword())
                || userInformation.getAuthority() == null) {
            throw new IllegalArgumentException("用户ID、密码、权限不能为空");
        }

        // 2. 查询用户是否存在（校验 userId 对应的用户）
        UserInformation dbUser = userInformationMapper.selectOne(
                new LambdaQueryWrapper<UserInformation>()
                        .eq(UserInformation::getId, userId)
        );
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 密码校验（此处假设密码已用 BCrypt 加密存储，需引入 BCrypt 依赖）
        // 注意：如果原密码是明文存储，需先批量加密后再修改此处逻辑（禁止明文校验）
        if (!BCrypt.checkpw(userInformation.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 4. 权限分配（使用传入的新权限更新）
        UserInformation updateUser = new UserInformation();
        updateUser.setId(userInformation.getUserId());  // 用数据库查询到的用户ID（确保准确性）
        updateUser.setAuthority(userInformation.getAuthority());  // 传入的新权限

        // 5. 执行更新（判断更新是否成功，增强健壮性）
        int updateCount = userInformationMapper.updateById(updateUser);
        if (updateCount != 1) {
            throw new RuntimeException("权限分配失败");
        }

        return true;
    }


    /**
     * 用户注册
     * @param userInformation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(UserInformation userInformation) {
        UserInformation isExist = userInformationMapper.selectOne(
                new LambdaQueryWrapper<UserInformation>()
                        .eq(UserInformation::getAccountNumber, userInformation.getAccountNumber())
        );
        if (isExist!=null) {
                throw new RuntimeException("账号已存在");
        }
        String encryptedPwd = BCrypt.hashpw(userInformation.getPassword(), BCrypt.gensalt(12));
        userInformation.setAccountNumber(userInformation.getAccountNumber());
        userInformation.setPassword(encryptedPwd);
        this.save(userInformation);
        return true;
    }

    @Override
    public boolean sendVerificationCode(String phone) {
        try {
            // 发送短信验证码
            String verificationCode = smsUtil.sendSmsVerificationCode(phone);
            
            // 将验证码存储到Redis中，设置过期时间
            redisTemplate.opsForValue().set("verification_code:" + phone, verificationCode, java.time.Duration.ofMinutes(5));
            
            log.info("短信验证码发送成功，手机号：{}", phone);
            return true;
        } catch (Exception e) {
            log.error("发送短信验证码失败，手机号：{}，错误信息：{}", phone, e.getMessage());
            return false;
        }
    }
}




