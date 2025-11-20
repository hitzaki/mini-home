# Mini Home - 桌面环境系统

一个现代化的跨平台桌面环境系统，支持PC端和移动端，提供类似操作系统的桌面体验。

## 功能特性

### 核心功能

- **现代化UI设计**：采用玻璃态、扁平化设计语言，支持多种主题
- **响应式设计**：完美适配PC端大屏幕和手机端触摸屏
- **桌面环境**：壁纸系统、图标拖拽、右键菜单
- **应用管理**：支持Web应用和未来原生应用，多种打开方式
- **应用组织**：全局应用列表、全局搜索、应用文件夹、应用盒子
- **桌面小组件**：时钟、日历、天气、性能监控等
- **回收站**：安全删除，支持恢复和自动清理
- **跨端同步**：PC端和移动端数据同步，桌面布局独立

### 技术栈

**前端：**
- uniapp + Vue3
- 轻量级设计，快速响应

**后端：**
- SpringBoot 2.7.18
- SaToken（JWT Token认证）
- MyBatis-Plus
- MySQL
- MinIO（对象存储）

## 项目结构

```
mini-home/
├── backend/              # 后端SpringBoot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/github/hitzaki/
│   │   │   │       ├── common/        # 通用类
│   │   │   │       ├── config/        # 配置类
│   │   │   │       ├── controller/    # 控制器
│   │   │   │       ├── entity/        # 实体类
│   │   │   │       ├── mapper/        # Mapper接口
│   │   │   │       └── service/       # 服务层
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-local.yml  # 本地配置（不提交Git）
│   │   │       └── db/
│   │   │           └── schema.sql     # 数据库表结构
│   └── pom.xml
├── frontend/            # 前端uniapp项目
│   ├── pages/           # 页面
│   │   ├── index/       # 桌面主页
│   │   ├── login/       # 登录页
│   │   ├── add-app/     # 添加应用
│   │   ├── settings/    # 设置页
│   │   └── recycle-bin/ # 回收站
│   ├── utils/           # 工具类
│   ├── store/           # Vuex状态管理
│   └── App.vue
└── README.md
```

## 快速开始

### 环境要求

- JDK 21
- Maven 3.6+
- MySQL 8.0+
- Redis
- MinIO
- Node.js 16+（前端开发）

### 后端部署

1. **配置数据库**
   ```sql
   CREATE DATABASE mini_home DEFAULT CHARSET utf8mb4;
   ```
   然后执行 `backend/src/main/resources/db/schema.sql` 创建表结构

2. **配置本地环境**
   
   复制并编辑 `backend/src/main/resources/application-local.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/mini_home?...
       username: your_username
       password: your_password
     redis:
       host: localhost
       port: 6379
       password: your_redis_password
   
   minio:
     endpoint: http://localhost:9000
     access-key: your_access_key
     secret-key: your_secret_key
     bucket-name: mini-home
   ```

3. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

   后端服务运行在：`http://localhost:8428/miniHome`

### 前端部署

1. **安装依赖**（如果使用HBuilderX，可跳过）
   ```bash
   cd frontend
   npm install
   ```

2. **配置API地址**
   
   编辑 `frontend/utils/request.js`，修改 `baseURL` 为你的后端地址

3. **运行项目**
   
   使用 HBuilderX 打开 `frontend` 目录，运行到浏览器或打包为App

## API接口说明

### 用户相关

- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `POST /user/info` - 获取当前用户信息（需登录）

### 应用相关

- `POST /app/add` - 添加应用（需登录）
- `POST /app/list` - 获取用户所有应用（需登录）
- `POST /app/desktop` - 获取桌面应用（需登录）
- `POST /app/updatePosition` - 更新应用位置（需登录）
- `POST /app/removeFromDesktop` - 从桌面移除（需登录）
- `POST /app/addToDesktop` - 添加到桌面（需登录）
- `POST /app/uninstall` - 卸载应用（需登录）
- `POST /app/search` - 搜索应用（需登录）
- `POST /app/update` - 更新应用（需登录）
- `POST /app/markAsRead` - 标记为已读（需登录）

### 设置相关

- `POST /settings/get` - 获取用户设置（需登录）
- `POST /settings/update` - 更新用户设置（需登录）

### 回收站相关

- `POST /recycleBin/list` - 获取回收站列表（需登录）
- `POST /recycleBin/restore` - 恢复项目（需登录）
- `POST /recycleBin/deletePermanently` - 彻底删除（需登录）

## 功能说明

### 应用类型

- **Web应用**：用户添加的网站链接，支持外部浏览器打开或内部窗口打开
- **原生应用**：系统内置功能模块（未来扩展），只能使用内部窗口打开

### 应用打开方式

- **外部跳转**：直接在浏览器新标签页中打开
- **内部窗口**：在系统内嵌的浏览器窗口中打开，提供统一的窗口控件

### 跨端同步规则

- PC端和移动端共享应用库，但桌面布局独立
- 新同步的应用会标记为"新应用"，用户可标记为已读

### 回收站

- 卸载的应用、文件或文件夹首先移至回收站
- 支持恢复和彻底删除
- 可设置自动清理规则（默认30天）

## 开发计划

- [ ] 完善小组件系统（日历、天气、性能监控等）
- [ ] 实现应用文件夹功能
- [ ] 实现应用盒子功能
- [ ] 实现自动获取网站图标和名称
- [ ] 实现文件上传到MinIO
- [ ] 实现定时任务自动清理回收站
- [ ] 添加更多主题
- [ ] 优化移动端体验

## 许可证

MIT License

