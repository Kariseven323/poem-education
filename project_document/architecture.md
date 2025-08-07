# 诗词交流鉴赏平台 - 架构设计文档

## 1. 系统架构概览

### 1.1 整体架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用层     │    │   后端服务层     │    │   数据存储层     │
│                │    │                │    │                │
│  React/Vue.js  │◄──►│  Spring Boot   │◄──►│     MySQL      │
│                │    │                │    │    MongoDB     │
│                │    │                │    │     Redis      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 1.2 技术栈选型
- **后端框架**: Spring Boot 2.7+
- **数据库**: MySQL 8.0 (主数据库) + MongoDB 5.0 (文档存储)
- **缓存**: Redis 6.0+
- **构建工具**: Maven
- **Java版本**: JDK 11+

### 1.3 架构特点
- **单模块架构**: 简化部署和维护
- **多数据源**: MySQL存储结构化数据，MongoDB存储诗词内容和评论
- **分层设计**: Controller → Service → Repository
- **缓存策略**: Redis缓存热点数据

## 2. 模块设计

### 2.1 核心模块划分
```
poem-education/
├── src/main/java/com/poem/education/
│   ├── controller/          # 控制器层
│   ├── service/            # 业务逻辑层
│   ├── repository/         # 数据访问层
│   ├── entity/             # 实体类
│   ├── dto/                # 数据传输对象
│   ├── config/             # 配置类
│   ├── security/           # 安全相关
│   ├── utils/              # 工具类
│   └── PoemEducationApplication.java
└── src/main/resources/
    ├── application.yml
    ├── mapper/             # MyBatis映射文件
    └── static/
```

### 2.2 功能模块
1. **用户模块 (User Module)**
   - 用户注册/登录
   - 个人资料管理
   - 权限控制

2. **诗词模块 (Poem Module)**
   - 诗词库管理
   - 分类体系
   - 搜索功能

3. **交流模块 (Community Module)**
   - 评论系统
   - 点赞收藏
   - 用户互动

4. **鉴赏模块 (Appreciation Module)**
   - 诗词解析
   - 文学赏析
   - 知识图谱

5. **创作模块 (Creation Module)**
   - 用户创作
   - AI评分
   - 雷达图生成

## 3. 数据存储策略

### 3.1 MySQL存储
- 用户基础信息
- 系统配置数据
- 关系型数据

### 3.2 MongoDB存储  
- 诗词内容
- 评论数据
- 创作作品
- 分析结果

### 3.3 Redis缓存
- 用户会话
- 热点诗词
- 搜索结果
- 统计数据
