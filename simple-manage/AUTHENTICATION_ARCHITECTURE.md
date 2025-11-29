# 双认证系统架构文档

## 📋 架构概述

本系统实现了**两套完全独立的认证系统**，通过 Spring Security 的多 `SecurityFilterChain` 机制实现完全隔离：

1. **脚本端卡密认证系统** - 用于脚本客户端
2. **管理端用户认证系统** - 用于管理后台

## 🏗️ 核心设计

### 1. 双 SecurityFilterChain 架构

```
请求 → Spring Security
         ↓
    [路径匹配]
         ↓
  ┌──────┴──────┐
  ↓             ↓
脚本端链      管理端链
(Order=1)    (Order=2)
```

#### 脚本端 SecurityFilterChain
- **路径**：`/open-api/script/**`
- **优先级**：`@Order(1)` (最高)
- **过滤器**：`CardTokenAuthenticationFilter`
- **认证方式**：卡密 Token
- **无需角色**：独立认证，不依赖角色

#### 管理端 SecurityFilterChain  
- **路径**：除脚本端外的所有路径
- **优先级**：`@Order(2)`
- **过滤器**：`JwtAuthenticationFilter`
- **认证方式**：JWT Token
- **用户角色**：`ROLE_USER`（可选）

### 2. 认证流程

#### 脚本端卡密登录
```
1. 脚本客户端 → POST /open-api/script/login
   - 参数：cardNo, cardPwd, deviceInfo

2. ScriptCardAuthService 验证
   - 检查卡密有效性
   - 验证设备绑定
   - 检查过期时间
   - 查询关联游戏

3. 生成 Card Token
   - Subject: "card:卡号"
   - 包含过期时间

4. 返回登录信息
   - cardToken
   - 卡密信息
   - 游戏列表

5. 后续请求携带 Token
   - Authorization: Bearer <token>
   - 或 Card-Token: <token>

6. CardTokenAuthenticationFilter 验证
   - 验证 Token 有效性
   - 检查 Subject 格式 (card:xxx)
   - 设置到 SecurityContext
```

#### 管理端用户登录
```
1. 管理客户端 → POST /auth/login
   - 参数：username, password

2. AuthService 验证
   - 检查用户名密码
   - 验证账号状态

3. 生成 JWT Token
   - Subject: 用户名
   - 包含过期时间

4. 返回登录信息
   - token
   - 用户信息

5. 后续请求携带 Token
   - Authorization: Bearer <token>

6. JwtAuthenticationFilter 验证
   - 验证 Token 有效性
   - 加载用户详情
   - 设置到 SecurityContext
```

## 📂 核心文件

### 配置类
- `SecurityConfig.java` - 双过滤链配置

### 过滤器
- `CardTokenAuthenticationFilter.java` - 脚本端认证
- `JwtAuthenticationFilter.java` - 管理端认证

### 服务类
- `ScriptCardAuthService.java` - 卡密认证服务
- `AuthService.java` - 用户认证服务
- `UserDetailsServiceImpl.java` - 用户详情加载

### 控制器
- `ScriptOpenApiController.java` - 脚本端接口
- `AuthController.java` - 管理端认证接口

## 🔐 权限配置

### 脚本端权限规则
```java
@Order(1)
SecurityFilterChain scriptSecurityFilterChain() {
    // 仅匹配 /open-api/script/**
    .securityMatcher("/open-api/script/**")
    
    // 登录接口无需认证
    .requestMatchers("/open-api/script/login", 
                     "/open-api/script/checkUser").permitAll()
    
    // 其他接口需要认证
    .anyRequest().authenticated()
}
```

### 管理端权限规则
```java
@Order(2)
SecurityFilterChain adminSecurityFilterChain() {
    // 登录接口无需认证
    .requestMatchers("/auth/login", 
                     "/auth/register").permitAll()
    
    // 公共资源
    .requestMatchers("/hello/**", "/files/**").permitAll()
    
    // 其他接口需要认证
    .anyRequest().authenticated()
}
```

## 🎯 关键特性

### 1. 完全隔离
- 两套认证系统互不干扰
- 不同的 SecurityFilterChain 处理不同路径
- 过滤器无需路径判断逻辑

### 2. Token 区分
- **管理端**：Subject 为用户名（如：`admin`）
- **脚本端**：Subject 为 `card:卡号`（如：`card:ABC123`）

### 3. 灵活认证
- 管理端支持用户名密码登录
- 脚本端支持卡密+设备信息登录
- 都使用 JWT Token 进行会话管理

### 4. 无状态设计
- 所有认证采用 JWT 无状态方式
- 不依赖 Session
- 易于水平扩展

## 📝 接口清单

### 脚本端接口
| 路径 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/open-api/script/login` | POST | 卡密登录 | ❌ |
| `/open-api/script/checkUser` | GET | 检查用户 | ❌ |
| `/open-api/script/verify` | POST | 验证Token | ✅ |
| `/open-api/script/card-info` | GET | 获取卡密信息 | ✅ |
| `/open-api/script/latest/{gameId}` | GET | 获取最新版本 | ✅ |
| `/open-api/script/pre-check/{gameId}` | GET | 预检查 | ✅ |

### 管理端接口
| 路径 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/auth/login` | POST | 用户登录 | ❌ |
| `/auth/current` | GET | 当前用户信息 | ✅ |
| `/auth/logout` | POST | 退出登录 | ✅ |
| `/script/**` | ALL | 脚本管理 | ✅ |
| `/user/**` | ALL | 用户管理 | ✅ |

## 🔧 扩展建议

### 1. 添加角色权限（可选）
如需细粒度权限控制，可为管理端用户添加不同角色：
- `ROLE_ADMIN` - 超级管理员
- `ROLE_MANAGER` - 普通管理员
- `ROLE_VIEWER` - 只读用户

### 2. 刷新Token机制
建议为长时间使用的客户端添加刷新Token：
- 短期访问Token（如 2小时）
- 长期刷新Token（如 7天）

### 3. 日志审计
为敏感操作添加审计日志：
- 登录/登出记录
- 卡密使用记录
- 设备绑定变更

## ⚠️ 注意事项

1. **Token 安全性**
   - Token 应该设置合理的过期时间
   - 敏感操作需要额外验证

2. **跨域配置**
   - 确保 CORS 配置正确
   - 前后端分离需要允许跨域请求

3. **错误处理**
   - 认证失败应返回统一的错误格式
   - 避免泄露敏感信息

4. **性能优化**
   - Token 验证应该高效
   - 避免频繁数据库查询
   - 考虑添加缓存机制
