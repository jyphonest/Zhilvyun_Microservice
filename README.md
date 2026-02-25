# AI旅游云平台 - Spring Cloud微服务架构

## 项目概述

这是一个基于Spring Cloud Alibaba的AI旅游云平台，采用微服务架构设计，提供旅游计划管理、智能推荐等功能。

## 技术栈

- **Spring Boot**: 2.7.8
- **Spring Cloud**: 2021.0.8
- **Spring Cloud Alibaba**: 2021.0.4.0
- **MyBatis-Plus**: 3.5.4.1
- **MySQL**: 8.0.33
- **Nacos**: 服务注册与配置中心
- **Sentinel**: 流量控制与熔断
- **Seata**: 分布式事务
- **OpenFeign**: 服务间调用
- **Druid**: 数据库连接池

## 项目模块

### 核心服务模块

1. **cloudalibaba-provider-payment9001** - 旅游服务提供者
   - 端口: 9001
   - 功能: 旅游计划管理、攻略信息处理
   - 数据库: zhilvyun_jiaoyp

2. **cloudalibaba-consumer-nacos-order83** - 订单服务消费者
   - 端口: 83
   - 功能: 订单管理、服务调用
   - 实现订单相关的业务逻辑，比如创建订单时，调用支付服务完成支付、调用库存服务扣减库存等，体现 “服务间调用” 和 “微服务拆分” 的思想。
     可能会用到 OpenFeign 或 RestTemplate 做服务调用，结合 Sentinel 做限流、降级。

3. **cloudalibaba-config-nacos-client3377** - 配置中心客户端
   - 端口: 3377
   - 功能: 配置管理
   - 验证 Nacos 配置中心与 Spring Cloud 项目的集成，可能包含配置监听、多环境配置（开发 / 测试 / 生产）加载等逻辑

### 基础设施模块

4. **cloudalibaba-sentinel-gateway9528** - 网关服务
   - 端口: 9528
   - 功能: 统一入口、路由转发
   - 结合 Sentinel 实现网关层的流量控制（比如限制某个接口的调用频率、限制某个 IP 的请求数）、熔断降级（调用下游服务超时或失败时，返回兜底响应）。
     可能还会做鉴权、日志记录、请求过滤（如黑名单 IP 拦截）等通用网关逻辑。

5. **cloudalibaba-sentinel-service8401** - Sentinel监控服务
   - 端口: 8401
   - 功能: 流量控制、熔断降级
   - 实现具体业务逻辑（比如用户服务、商品服务等，需结合代码确认），同时集成 Sentinel，对自己的接口做限流（如 /user/info 接口每秒最多允许 100 次调用）、降级（调用数据库超时后，返回默认数据）。
     注册到 Nacos，供网关或其他服务发现调用，验证 Sentinel 在 “服务提供者” 侧的配置和效果

### 分布式事务模块

6. **seata-order-service2001** - 订单服务
   - 端口: 2001
   - 功能: 订单处理

7. **seata-storage-service2002** - 库存服务
   - 端口: 2002
   - 功能: 库存管理

8. **seata-account-service2003** - 账户服务
   - 端口: 2003
   - 功能: 账户管理

### 工具模块

9. **cloud_api_commons** - 公共API模块
   - 功能: 通用响应、异常处理 
   - 作为多模块项目的 “公共依赖”，让其他业务模块（如 provider、consumer）可以复用这些通用代码，避免重复开发。
   - 比如订单服务、支付服务都需要用 Result 统一返回对象，就可以定义在这个模块里。

10. **mybatis_generator2025** - 代码生成器
    - 功能: MyBatis-Plus代码自动生成

## 启动顺序

1. **Nacos服务** (端口: 8848)
2. **Sentinel Dashboard** (端口: 8080)
3. **MySQL数据库**
4. **各微服务模块**

## 数据库配置

### 主要数据库: zhilvyun_jiaoyp

包含以下主要表：
- `travel_plan_message` - 旅游计划基本信息
- `travel_plan_detail` - 旅游计划详细攻略

### 数据库连接配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/zhilvyun_jiaoyp?useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useSSL=false&autoReconnect=true&failOverReadOnly=false
    username: root
    password: 123456
    # Druid连接池配置
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
```

### MyBatis-Plus配置

```yaml
mybatis-plus:
  type-aliases-package: com.atjiao.cloud.domain
  mapper-locations: classpath*:mapper/*Mapper.xml
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  type-handlers-package: com.atjiao.cloud.config.handler
```

## 核心功能

### 旅游计划管理

1. **创建旅游计划**
   - 基本信息：标题、出发地、目的地、预算、天数
   - 详细攻略：游玩路线、交通、餐饮、住宿

2. **攻略信息处理**
   - 支持JSON格式的路线和图片信息
   - 自定义TypeHandler处理List<String>类型

3. **智能推荐**
   - 基于用户偏好的旅游路线推荐
   - 预算优化建议

## 技术特性

### MyBatis-Plus集成

- 自动CRUD操作
- 自定义TypeHandler处理复杂数据类型
- 分页查询支持
- 逻辑删除

### 微服务特性

- 服务注册与发现 (Nacos)
- 配置中心 (Nacos Config)
- 服务熔断 (Sentinel)
- 分布式事务 (Seata)

### 监控与日志

- 详细的SQL日志输出
- 服务健康检查
- 性能监控

## 常见问题解决

### 1. FactoryBean错误解决方案

**问题**: `java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String`

**解决方案**:
1. **升级MyBatis-Plus版本** - 从3.5.3.1升级到3.5.4.1
2. **删除冲突的Bean定义** - 移除手动创建的ObjectMapper Bean和MyBatisConfig
3. **修复依赖版本** - 使用jakarta.persistence替代javax.persistence
4. **简化MyBatis-Plus配置** - 移除可能导致问题的配置项
5. **正确注册TypeHandler** - 使用yml的type-handlers-package自动注册
6. **清理依赖缓存** - 删除本地Maven仓库中的老版本依赖

### 2. MyBatis-Plus版本兼容性问题

**问题**: MyBatis-Plus版本与Spring Boot 3.2.0不兼容

**解决方案**:
- 确保MyBatis-Plus版本与Spring Boot版本兼容
- Spring Boot 3.2.0需要使用MyBatis-Plus 3.5.4.1或更高版本
- 正确配置实体类注解
- 使用提供的清理脚本彻底清理老版本依赖

### 3. 实体类配置

确保实体类正确配置MyBatis-Plus注解：

```java
@TableName("travel_plan_detail")
public class TravelPlanDetail {
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;
    
    @TableField(value = "travel_route", typeHandler = ListTypeHandler.class)
    private List<String> travelRoute;
}
```

### 4. TypeHandler配置

对于复杂数据类型，需要自定义TypeHandler：

```java
@MappedTypes(List.class)
public class ListTypeHandler extends BaseTypeHandler<List<String>> {
    // 实现JSON与List的转换
}
```

### 5. 依赖注入问题

**问题**: FileServiceImpl依赖注入问题

**解决方案**:
1. 清理未使用的导入
2. 统一依赖注入方式
3. 使用接口而不是实现类
4. 添加配置验证

## 开发环境要求

- JDK: 17+
- Maven: 3.6+
- MySQL: 8.0+
- Nacos: 2.0+

## 最新配置完善 (2024-01-01)

### 配置完善内容

1. **数据库连接配置**
   - 完善了MySQL数据库连接配置
   - 添加了Druid连接池配置
   - 配置了数据库连接监控

2. **MyBatis-Plus集成**
   - 添加了MyBatis-Plus依赖
   - 配置了分页插件、乐观锁插件
   - 创建了自定义TypeHandler处理List<String>类型
   - 配置了自动填充处理器

3. **实体类设计**
   - 创建了TravelPlanDetail实体类
   - 使用了MyBatis-Plus注解
   - 支持逻辑删除和乐观锁

4. **测试接口**
   - 创建了数据库连接测试接口
   - 添加了服务健康检查接口
   - 提供了服务信息查询接口

### 测试接口

启动服务后，可以通过以下接口测试配置：

- `GET /test/db` - 测试数据库连接
- `GET /test/health` - 服务健康检查
- `GET /test/info` - 获取服务信息

### 监控功能

- Druid监控面板：`http://localhost:9001/druid`
  - 用户名：admin
  - 密码：123456

## 部署说明

1. 克隆项目到本地
2. 配置数据库连接信息
3. 启动Nacos和Sentinel
4. 按顺序启动各微服务模块
5. 访问Swagger UI进行接口测试

## 接口文档

- 9001端口: http://localhost:9001/swagger-ui/index.html
- 其他端口: 根据具体服务配置访问

## 联系方式

- 作者: jyp
- 项目创建时间: 2025年5月

## 依赖清理脚本

### 快速清理脚本

项目提供了两个清理脚本，用于解决MyBatis-Plus版本冲突问题：

#### PowerShell脚本 (cleanup-dependencies.ps1)
```powershell
# 在项目根目录执行
.\cleanup-dependencies.ps1
```

#### 批处理脚本 (cleanup-dependencies.bat)
```cmd
# 在项目根目录执行
cleanup-dependencies.bat
```

#### 手动清理步骤
如果脚本无法执行，可以手动执行以下步骤：

1. **清理target目录**
   ```cmd
   rmdir /s /q target
   ```

2. **清理本地Maven仓库**
   ```cmd
   rmdir /s /q "D:\repo\com\baomidou\mybatis-plus-boot-starter\3.5.3.1"
   rmdir /s /q "D:\repo\com\baomidou\mybatis-plus\3.5.3.1"
   rmdir /s /q "D:\repo\com\baomidou\mybatis-plus-extension\3.5.3.1"
   rmdir /s /q "D:\repo\com\baomidou\mybatis-plus-core\3.5.3.1"
   rmdir /s /q "D:\repo\com\baomidou\mybatis-plus-annotation\3.5.3.1"
   ```

3. **强制重新下载依赖**
   ```cmd
   mvn clean install -U
   ```

4. **在IDEA中刷新项目**
   - 右键项目 -> Maven -> Reload Project

## 更新日志

### 2025-07-05
- 彻底解决FactoryBean错误问题
- 升级MyBatis-Plus版本到3.5.4.1
- 删除冲突的MyBatisConfig和ObjectMapper Bean
- 修复所有seata模块的javax.persistence依赖问题
- 添加依赖清理脚本
- 完善项目文档和问题解决方案

### 2025-05-26
- 修复FactoryBean错误问题
- 优化MyBatis-Plus配置
- 完善TypeHandler实现
- 修复依赖注入问题
- 添加详细的项目文档 