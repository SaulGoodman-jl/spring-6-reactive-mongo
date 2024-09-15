# Spring Reactive Mongo 项目

## 项目简介
该项目基于Spring Reactive Web构建，采用非阻塞式编程模型，集成了Spring Data Reactive MongoDB用于处理MongoDB数据库操作。项目通过OAuth2资源服务器实现JWT认证，并引入Lombok简化开发，支持数据验证。

## 技术栈
- **框架**: Spring Reactive Web
- **数据库**: Spring Data Reactive MongoDB
- **安全**: OAuth2 Resource Server
- **其他**: Lombok, Validation

## 功能
1. **非阻塞式API**：提供响应式API接口。
2. **MongoDB支持**：通过Spring Data Reactive MongoDB进行响应式数据库交互。
3. **安全认证**：集成OAuth2资源服务器，使用JWT进行用户认证。
4. **数据验证**：支持请求参数的验证。

## 安装步骤
1. 克隆项目：
   ```bash
   git clone https://github.com/SaulGoodman-jl/spring-6-reactive-mongo.git
   ```
2. 启动应用：
   ```bash
   .\mvnw spring-boot:run
   ```

## 配置
在[application.properties](src/main/resources/application.properties)中可以修改MongoDB连接配置和JWT验证配置。

## License
该项目基于MIT许可证，详情见[LICENSE](./LICENSE)。