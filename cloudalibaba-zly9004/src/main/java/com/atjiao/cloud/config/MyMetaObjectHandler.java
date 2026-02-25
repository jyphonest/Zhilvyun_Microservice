package com.atjiao.cloud.config;

import com.atjiao.cloud.helper.LoginHelper;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

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
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 设置创建时间
        this.setFieldValByName("createTime", new Date(), metaObject);
        // 设置更新时间
        this.setFieldValByName("updateTime", new Date(), metaObject);
        // 设置创建者和更新者
        try {
            Long userId = LoginHelper.getUserId();
            String userIdStr = userId != null ? userId.toString() : null;
            this.setFieldValByName("createBy", userIdStr, metaObject);
            this.setFieldValByName("updateBy", userIdStr, metaObject);
        } catch (Exception e) {
            // 在消息队列处理等非用户会话场景下，设置系统用户ID
            log.debug("自动填充调试 => 非用户会话场景，使用系统用户ID");
            this.setFieldValByName("createBy", "1001", metaObject); // 系统用户ID
            this.setFieldValByName("updateBy", "1001", metaObject);
        }
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        // 设置更新者
        try {
            Long userId = LoginHelper.getUserId();
            String userIdStr = userId != null ? userId.toString() : null;
            this.setFieldValByName("updateBy", userIdStr, metaObject);
        } catch (Exception e) {
            // 在消息队列处理等非用户会话场景下，设置系统用户ID
            log.debug("自动填充调试 => 非用户会话场景，使用系统用户ID");
            this.setFieldValByName("updateBy", "1001", metaObject); // 系统用户ID
        }
    }

}