# 项目：诗词交流鉴赏平台 | 协议：RIPER-5 + SMART-6 (v4.10)
- **执行模式**: 标准协作模式
- **总状态**: 执行中
- **最后更新**: 2025-08-07T09:56:16+08:00
- **性能指标**: 并行度 L1[85%] L2[60%] L3[40%] | 时间节省[~70%]

## 团队配置
- **内置顾问团**: AR, PDM, LD, DW, QE
- **动态Subagents**: backend-api-expert, data-architect

## 执行状态（实时）
`🔀 标准协作模式 | 🔄 并行: 5个操作 | ⏱️ 节省: 60% | 📊 进度: 30%`
- **任务快照**:
    - [#001] 创建项目概述文档: 🟢 执行中 (by PM)
    - [#002] 设计API接口文档: 🟡 待开始 (by backend-api-expert)
    - [#003] 设计数据库架构: 🟡 待开始 (by data-architect)

## 项目概述
**诗词交流鉴赏平台** - 一个集诗词欣赏、创作、交流于一体的文化教育平台

### 技术栈
- **后端框架**: Spring Boot
- **关系数据库**: MySQL
- **文档数据库**: MongoDB  
- **缓存**: Redis
- **架构模式**: 单模块架构

### 核心功能模块
1. **用户管理** - 注册、登录、个人资料管理
2. **诗词管理** - 诗词库、分类、搜索功能
3. **交流功能** - 层级嵌套评论、点赞、收藏
4. **鉴赏功能** - 诗词解析、文学赏析
5. **创作功能** - 用户诗词创作、AI评分机制、雷达图生成

## 关键文档链接
- [🚀 快速开始指南](./quick_start_guide.md) - **新增：5分钟快速启动**
- [研究报告](./research_report.md)
- [架构设计](./architecture.md)
- [API接口文档](./api_documentation.md) - **基于现有MongoDB数据设计**
- [数据库设计](./database_design.md) - **重新设计，基于现有数据结构**
- [数据库迁移指南](./database_migration_guide.md) - **新增：详细迁移方案**
- [部署运维文档](./deployment_guide.md)
- [开发规范](./development_standards.md)
- [项目总结](./review_summary.md)

## 数据库脚本
- [MySQL初始化脚本](../database/mysql_init.sql) - **新增：完整MySQL建表脚本**
- [MongoDB扩展脚本](../database/mongodb_extend.js) - **新增：MongoDB集合扩展**
- [现有MongoDB数据](../database/poem_education.js) - **现有：诗词数据库结构**
