# AI模拟面试后端技术栈及设计思路总结

## 一、技术栈

### 1. 核心框架与开发语言
- **开发语言**：Java
- **核心框架**：Spring Boot（快速构建后端应用，提供依赖注入、自动配置等核心功能）
- **Web框架**：Spring MVC（处理HTTP请求，实现RESTful API接口）

### 2. 数据存储
- **关系型数据库**：基于MyBatis-Plus操作，存储结构化数据，包括：
  - 用户信息（`User`表）
  - 面试基本信息（`interview`表）
  - 面试状态（`interview_state`表）
- **非关系型数据库**：MongoDB，存储非结构化/半结构化数据，包括：
  - 面试过程记录（`interviews`集合）
  - 问题与回答（`Question`、`Answer`文档）
  - 面试时间线事件（`TimelineEvent`）

### 3. 接口文档与跨域处理
- **接口文档**：Swagger/OpenAPI（通过`OpenAPIConfig`配置，生成“面试系统API文档”，包含接口标题、版本和描述）
- **跨域配置**：`CorsConfig`实现跨域支持，允许所有域名、请求头、请求方法，支持携带凭证（如Cookie）

### 4. 网络请求工具
- **WebClient**：`WebClientConfig`配置异步非阻塞HTTP客户端，设置30秒响应超时
- **RestTemplate**：在`ApiService`中用于调用第三方API（如文件上传、情感分析接口）

### 5. 安全认证
- **JWT（JSON Web Token）**：`JwtHelper`工具类实现：
  - 生成用户身份令牌（包含用户ID）
  - 解析令牌中的用户ID
  - 验证令牌有效期
- **登录拦截器**：`LoginProtectedInterceptor`拦截`/interview/**`路径请求，验证JWT令牌有效性，未登录则拦截

### 6. 其他工具与组件
- **密码加密**：`MD5Util`对用户密码进行MD5加密存储
- **视频处理**：`VideoToMp3Converter`使用FFmpeg从视频中提取MP3音频
- **结果封装**：`Result`与`ResultCodeEnum`统一接口返回格式（包含状态码、消息、数据）

## 二、设计思路

### 1. 架构设计
- **分层架构**：
  - 控制层（`Controller`）：处理HTTP请求（如`InterviewController`、`UserController`）
  - 服务层（`Service`）：实现业务逻辑（如`InterviewServiceImpl`处理面试流程）
  - 数据访问层（`Mapper`）：操作数据库（基于MyBatis-Plus的`BaseMapper`）
  - 实体层（`pojo`）：定义数据模型（区分关系型与MongoDB文档实体）
  - 工具层（`utils`）：提供通用功能（加密、认证、结果封装等）

### 2. 业务流程设计
- **用户模块**：
  - 注册：检查用户名唯一性，MD5加密密码后存入数据库
  - 登录：验证账号密码，生成JWT令牌返回
  - 信息查询：通过令牌解析用户ID，查询并返回用户信息

- **面试模块**：
  1. **创建面试**：
     - 检查用户是否有未完成面试
     - 先存入MySQL（`interview`表），再调用AI服务上传简历与岗位文件
     - 生成匹配分析结果，存入MongoDB（`interviewDocument`）
  2. **开始面试**：
     - 更新面试状态为“started”，获取初始问题并存入MongoDB
  3. **问题与回答**：
     - AI生成下一个问题（基于历史回答）
     - 接收用户回答文本与视频，提取音频并调用情感分析API，存储分析结果
  4. **完成面试**：
     - 生成面试总结，更新面试状态为“completed”

### 3. 数据存储设计
- **分库策略**：
  - 关系型数据库：存储核心结构化数据（用户ID、面试ID、状态等），支持事务与关联查询
  - MongoDB：存储面试过程中的动态数据（问题、回答、时间线），适合高频写入与非结构化数据
- **事务处理**：创建面试时，若MongoDB插入或AI调用失败，回滚MySQL中的面试记录

### 4. 扩展性设计
- **第三方服务集成**：通过`ApiService`封装第三方API调用（文件上传、情感分析、问题生成），降低耦合
- **配置化设计**：跨域、JWT过期时间、第三方API地址等通过配置类或注解灵活设置
- **拦截器机制**：通过`WebMVCConfig`注册拦截器，可灵活扩展权限控制逻辑