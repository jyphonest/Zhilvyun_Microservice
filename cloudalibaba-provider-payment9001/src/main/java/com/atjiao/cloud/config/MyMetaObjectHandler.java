package com.atjiao.cloud.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import com.atjiao.cloud.helper.LoginHelper;

/**
 * MyBatis-Plus自动填充处理器
 * 用于自动填充创建时间和更新时间
 * 
 * @author atjiao
 * @since 2024-01-01
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的填充策略
     * 
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始执行插入填充...");
        
        // 设置创建时间
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        
        // 设置更新时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        
        // 设置创建者和更新者
        try {
            Long userId = LoginHelper.getUserId();
            String userIdStr = userId != null ? userId.toString() : null;
            this.setFieldValByName("createBy", userIdStr, metaObject);
            this.setFieldValByName("updateBy", userIdStr, metaObject);
        } catch (Exception e) {
            log.warn("自动填充警告 => 用户未登录");
        }
        
        log.info("插入填充完成");
    }

    /**
     * 更新时的填充策略
     * 
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始执行更新填充...");
        
        // 设置更新时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        
        // 设置更新者
        try {
            Long userId = LoginHelper.getUserId();
            String userIdStr = userId != null ? userId.toString() : null;
            this.setFieldValByName("updateBy", userIdStr, metaObject);
        } catch (Exception e) {
            log.warn("自动填充警告 => 用户未登录");
        }
        
        log.info("更新填充完成");
    }
} 