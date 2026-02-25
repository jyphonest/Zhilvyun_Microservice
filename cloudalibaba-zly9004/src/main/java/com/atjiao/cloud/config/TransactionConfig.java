package com.atjiao.cloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

/**
 * 事务配置类
 * 用于确保数据库事务正确管理
 *
 * @author 焦叶鹏
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * Spring Boot会自动配置DataSourceTransactionManager
     * 这里显式声明以确保事务管理器正确配置
     * 注意：如果你的数据源不是默认的，可能需要手动配置
     */
    // 如果需要自定义事务管理器，可以取消注释以下代码
    /*
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    */
}