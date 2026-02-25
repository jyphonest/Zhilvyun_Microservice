# AI旅游云平台 - 通用配置模块 (cloud_api_commons)

## 模块概述

`cloud_api_commons` 是AI旅游云平台的通用配置模块，提供了所有微服务模块共用的配置项，包括：

- **Sa-Token认证配置** - JWT token管理、权限验证
- **Spring Security安全配置** - 安全策略、路径排除
- **数据库配置** - MySQL连接、Druid连接池
- **MyBatis-Plus配置** - ORM框架配置、TypeHandler
- **日志配置** - 日志级别、输出格式、文件管理
- **Redis缓存配置** - 缓存策略、序列化配置
- **文件上传配置** - 文件存储、OSS集成
- **业务配置** - 旅游计划、用户管理等业务参数

## 快速开始

### 1. 添加依赖

在需要使用通用配置的模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atjiao.cloud</groupId>
    <artifactId>cloud_api_commons</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 引入配置

在模块的 `application.yml` 中引入通用配置：

```yaml
# 引入通用配置
spring:
  profiles:
    include: common
```

或者直接复制需要的配置项到模块的配置文件中。

### 3. 使用配置类

在代码中注入配置类：

```java
@Autowired
private CommonConfig commonConfig;

@Autowired
private SecurityConfig securityConfig;

@Autowired
private CacheConfig cacheConfig;
```

## 配置项详解

### Sa-Token配置

```yaml
sa-token:
  token-name: Authorization          # token名称
  timeout: 1296000                  # token有效期(15天)
  active-timeout: 1296000           # 活跃超时时间
  is-concurrent: false              # 是否允许并发登录
  is-share: false                   # 是否共享token
  is-read-header: true              # 从header读取token
  token-prefix: "Bearer"            # token前缀
  jwt-secret-key: abcdefghijklmnopqrstuvwxyz  # JWT密钥
```

**使用示例：**
```java
// 登录
StpUtil.login(10001);

// 获取当前登录用户ID
Object loginId = StpUtil.getLoginId();

// 检查是否登录
boolean isLogin = StpUtil.isLogin();

// 注销
StpUtil.logout();
```

### Spring Security配置

```yaml
security:
  excludes:                         # 排除路径列表
    - /*.html
    - /**/*.css
    - /**/*.js
    - /auth/login
    - /actuator/**
  enabled: true                     # 是否启用安全验证
  csrf-enabled: false               # 是否启用CSRF保护
  session-timeout: 1800             # 会话超时时间
```

**使用示例：**
```java
@Autowired
private SecurityConfig securityConfig;

// 检查路径是否需要安全验证
public boolean isExcludedPath(String path) {
    return securityConfig.getExcludes().stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, path));
}
```

### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/zhilvyun_jiaoyp?...
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5               # 初始连接数
      min-idle: 5                   # 最小连接数
      max-active: 20                # 最大连接数
      max-wait: 60000               # 获取连接超时时间
      validation-query: SELECT 1    # 连接验证SQL
```

**Druid监控面板：**
- 访问地址：`http://localhost:端口/druid`
- 用户名：admin
- 密码：123456

### MyBatis-Plus配置

```yaml
mybatis-plus:
  type-aliases-package: com.atjiao.cloud.entity
  mapper-locations: classpath*:mapper/*Mapper.xml
  configuration:
    map-underscore-to-camel-case: true  # 驼峰命名
    cache-enabled: true                 # 开启缓存
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      id-type: auto                     # 主键类型
      logic-delete-field: deleted       # 逻辑删除字段
      logic-delete-value: 1             # 逻辑删除值
      logic-not-delete-value: 0         # 逻辑未删除值
  type-handlers-package: com.atjiao.cloud.config.handler  # 自定义TypeHandler
```

**使用示例：**
```java
// 实体类
@TableName("travel_plan_message")
public class TravelPlanMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableLogic
    private Integer deleted;
}

// Mapper接口
@Mapper
public interface TravelPlanMessageMapper extends BaseMapper<TravelPlanMessage> {
}
```

### 日志配置

```yaml
logging:
  level:
    com.atjiao.cloud: debug          # 项目包日志级别
    com.baomidou.mybatisplus: debug  # MyBatis-Plus日志级别
    org.springframework.web: debug   # Spring Web日志级别
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
  file:
    name: logs/ai-tourism-cloud.log  # 日志文件路径
    max-size: 100MB                  # 单个文件最大大小
    max-history: 30                  # 保留文件数量
```

### Redis缓存配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 8                # 最大连接数
        max-idle: 8                  # 最大空闲连接数
        min-idle: 0                  # 最小空闲连接数
```

**使用示例：**
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 设置缓存
redisTemplate.opsForValue().set("user:10001", userInfo, Duration.ofMinutes(30));

// 获取缓存
UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get("user:10001");
```

### 业务配置

```yaml
business:
  travel:
    max-plan-count: 100              # 最大计划数量
    max-strategy-length: 10000       # 最大攻略长度
    default-budget: 5000             # 默认预算
    default-days: 7                  # 默认天数
  user:
    password-min-length: 6           # 密码最小长度
    password-max-length: 20          # 密码最大长度
    username-min-length: 3           # 用户名最小长度
    username-max-length: 20          # 用户名最大长度
    email-code-expire: 5             # 邮箱验证码有效期(分钟)
    phone-code-expire: 5             # 手机验证码有效期(分钟)
```

**使用示例：**
```java
@Autowired
private CommonConfig commonConfig;

// 验证旅游计划数量
public boolean validatePlanCount(int count) {
    return count <= commonConfig.getTravel().getMaxPlanCount();
}

// 验证密码长度
public boolean validatePassword(String password) {
    int length = password.length();
    return length >= commonConfig.getUser().getPasswordMinLength() 
        && length <= commonConfig.getUser().getPasswordMaxLength();
}
```

### 缓存配置

```yaml
cache:
  default-ttl: 3600                 # 默认缓存时间(秒)
  user-ttl: 1800                    # 用户信息缓存时间(秒)
  travel-plan-ttl: 7200             # 旅游计划缓存时间(秒)
  code-ttl: 300                     # 验证码缓存时间(秒)
  enabled: true                     # 是否启用缓存
  type: redis                       # 缓存类型
```

### 文件上传配置

```yaml
file:
  upload:
    path: /upload/                   # 上传路径
    max-size: 10MB                  # 最大文件大小
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx  # 允许的文件类型
    use-oss: false                  # 是否使用OSS
    storage-type: local             # 存储类型
    enabled: true                   # 是否启用文件上传
```

### 跨域配置

```yaml
cors:
  allowed-origins: "*"              # 允许的域名
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS  # 允许的方法
  allowed-headers: "*"              # 允许的请求头
  allow-credentials: true           # 是否允许携带凭证
  max-age: 3600                     # 预检请求有效期(秒)
  enabled: true                     # 是否启用跨域
```

### 线程池配置

```yaml
thread:
  pool:
    core-size: 10                   # 核心线程数
    max-size: 20                    # 最大线程数
    queue-capacity: 200             # 队列容量
    keep-alive: 60                  # 线程空闲时间(秒)
    name-prefix: ai-tourism-        # 线程名前缀
    enabled: true                   # 是否启用线程池
```

## 环境配置

### 开发环境 (dev)

```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/zhilvyun_jiaoyp?...
  redis:
    host: localhost
    port: 6379
logging:
  level:
    com.atjiao.cloud: debug
```

### 测试环境 (test)

```yaml
spring:
  profiles:
    active: test
  datasource:
    url: jdbc:mysql://test-server:3306/zhilvyun_jiaoyp?...
  redis:
    host: test-server
    port: 6379
logging:
  level:
    com.atjiao.cloud: info
```

### 生产环境 (prod)

```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://prod-server:3306/zhilvyun_jiaoyp?...
  redis:
    host: prod-server
    port: 6379
logging:
  level:
    com.atjiao.cloud: warn
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
```

## 监控端点

### 健康检查

```bash
# 健康检查
GET /actuator/health

# 详细信息
GET /actuator/health/details
```

### 应用信息

```bash
# 应用信息
GET /actuator/info

# 环境信息
GET /actuator/env

# 配置信息
GET /actuator/configprops
```

### 指标监控

```bash
# 指标信息
GET /actuator/metrics

# 特定指标
GET /actuator/metrics/jvm.memory.used
```

## 常见问题

### 1. 配置不生效

**问题：** 配置项修改后不生效

**解决方案：**
- 检查配置文件路径是否正确
- 确认配置项名称是否正确
- 重启应用使配置生效
- 检查是否有其他配置文件覆盖

### 2. 数据库连接失败

**问题：** 无法连接到数据库

**解决方案：**
- 检查数据库服务是否启动
- 确认数据库连接信息是否正确
- 检查防火墙设置
- 验证数据库用户权限

### 3. Redis连接失败

**问题：** 无法连接到Redis

**解决方案：**
- 检查Redis服务是否启动
- 确认Redis连接信息是否正确
- 检查Redis密码设置
- 验证网络连接

### 4. 文件上传失败

**问题：** 文件上传失败

**解决方案：**
- 检查文件大小是否超限
- 确认文件类型是否允许
- 检查存储路径权限
- 验证磁盘空间

### 5. 跨域问题

**问题：** 前端请求出现跨域错误

**解决方案：**
- 检查CORS配置是否正确
- 确认允许的域名设置
- 验证请求方法是否允许
- 检查请求头设置

## 最佳实践

### 1. 配置管理

- 使用环境变量管理敏感信息
- 将配置按功能模块分组
- 使用配置中心统一管理配置
- 定期备份配置文件

### 2. 安全配置

- 定期更新密钥和密码
- 使用HTTPS协议
- 启用安全日志记录
- 定期进行安全审计

### 3. 性能优化

- 合理设置连接池参数
- 优化SQL查询语句
- 使用缓存减少数据库访问
- 监控系统资源使用情况

### 4. 日志管理

- 设置合适的日志级别
- 定期清理日志文件
- 使用日志聚合工具
- 监控日志异常情况

## 版本历史

### v1.0.0 (2024-01-01)

- 初始版本发布
- 支持Sa-Token认证
- 支持Spring Security安全
- 支持MyBatis-Plus配置
- 支持Redis缓存
- 支持文件上传
- 支持跨域配置

## 贡献指南

欢迎提交Issue和Pull Request来改进这个模块。

## 许可证

本项目采用MIT许可证。 