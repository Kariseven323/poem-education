# Poem Education 项目说明

> 现代诗歌教育平台，含 Java 后端与 React 前端，支持用户创作、检索、社区互动与安全登录。

## 1. 项目概览
- 后端：Java + Spring Boot（Maven 构建）、JWT 认证、日志落地到 logs/
- 前端：React（Node.js 工具链），支持本地开发与生产构建
- 数据库：MySQL + MongoDB + Redis（按需启用；详见 application.yml）
- 文档：位于 project_document/ 目录，涵盖架构、接口、部署与修复记录

相关文档（建议先读）：
- project_document/architecture.md
- project_document/api_documentation.md
- project_document/quick_start_guide.md
- project_document/deployment_guide.md

目录结构（部分）：
- pom.xml                    Maven 项目定义（后端）
- src/                       后端源码与测试
- frontend/                  前端工程（React）
- logs/                      运行日志
- project_document/          项目文档与运维手册
- target/                    后端构建产物（.jar）

## 2. 运行前置条件
后端：
- Java 11 或以上（建议 LTS；pom.xml 当前为 11）
- Maven 3.8+（建议 3.9+）
- MongoDB 实例（本地或远程）

前端：
- Node.js 18+（建议）
- npm 9+ 或对应包管理器

## 3. 配置说明（后端）
应用配置位于 `src/main/resources/application.yml`（按 profile 分层：dev/test/prod）。
常用环境变量或属性（示例为环境变量名；Spring Boot 支持下划线转点号的松散绑定）：
- SERVER_PORT：后端服务端口（默认 8080）
- 数据库 - MySQL：
  - SPRING_DATASOURCE_URL（例：jdbc:mysql://localhost:3306/poem_education?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true）
  - SPRING_DATASOURCE_USERNAME / SPRING_DATASOURCE_PASSWORD
- 数据库 - MongoDB（两种方式二选一）：
  - SPRING_DATA_MONGODB_URI（例：mongodb://localhost:27017/poem_education）
  - 或 SPRING_DATA_MONGODB_HOST / SPRING_DATA_MONGODB_PORT / SPRING_DATA_MONGODB_DATABASE
- 缓存 - Redis：SPRING_REDIS_HOST / SPRING_REDIS_PORT / SPRING_REDIS_PASSWORD（可选）
- 安全 - JWT：JWT_SECRET、JWT_EXPIRATION（参考 application.yml 的 jwt.*）
- AI 评分服务：AI_SCORE_API_URL、AI_SCORE_API_KEY、AI_SCORE_MODEL、AI_SCORE_TIMEOUT、AI_SCORE_MOCK_ENABLED 等

在 Windows PowerShell 下临时设置示例：
```
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/poem_education?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "<your-password>"
$env:SPRING_DATA_MONGODB_URI = "mongodb://localhost:27017/poem_education"
$env:JWT_SECRET = "replace-with-a-strong-secret"
$env:SERVER_PORT = "8080"
$env:AI_SCORE_API_URL = "http://127.0.0.1:1234"
```

更多安全与配置建议参见：
- project_document/jwt_analysis_report.md
- project_document/jwt_security_fix_summary.md
- project_document/config_placeholder_fix.md

## 4. 启动与调试
### 4.1 启动后端（本地开发）
在仓库根目录执行：
```
mvn spring-boot:run
```
或构建后运行：
```
mvn clean package
java -jar target/poem-education-1.0.0.jar
```
默认地址类似：`http://localhost:8080`（以实际端口为准）

### 4.2 启动前端（本地开发）
进入前端目录并安装依赖：
```
cd frontend
npm install
```
开发模式运行：
```
npm start
```
生产构建：
```
npm run build
```
构建产物默认输出到 `frontend/build/`（以实际脚手架配置为准）。

> 前端详细脚本与说明请参考：frontend/README.md 与 frontend/package.json（开发时跨域代理见 package.json 中的 proxy，默认指向 http://localhost:8080）

## 5. 数据库与样例数据
- 默认依赖 MySQL 与 MongoDB（部分模块使用），请确保均已正确配置；Redis 可选。
- MySQL 默认开发配置见 application.yml（jdbc:mysql://localhost:3306/poem_education）。请根据实际环境修改用户名/密码，勿在生产环境使用仓库中的默认密码。
- MongoDB 可通过 SPRING_DATA_MONGODB_URI 或 host/port/database 进行配置。
- 仓库根目录存在 `database.7z`，如需导入样例/备份数据，可参考 project_document/database_migration_guide.md 进行解压和恢复（请在测试环境操作，不要覆盖生产数据）。
- 与类型、ObjectId、搜索等相关的已知修复与注意事项可参考：
  - project_document/objectid_fix_summary.md
  - project_document/type_search_fix.md
  - project_document/writer_search_fix.md
  - project_document/enhanced_search_summary.md

## 6. API 文档
- 后端 API 说明与示例请见：project_document/api_documentation.md
- 鉴权相关（JWT）与错误处理建议：
  - project_document/jwt_fix_summary.md
  - project_document/comment_400_error_analysis.md

## 7. 日志与排障
- 运行日志默认输出到 `logs/` 目录，例如：
  - logs/poem-education.log
  - logs/poem-education.log.YYYY-MM-DD.0.gz
- 常见启动问题与排障建议：project_document/startup_issue_resolution.md
- 代表性问题分析：
  - project_document/debug_log_analysis.md
  - project_document/compilation_fix_summary.md
  - project_document/fix_summary.md

## 8. 测试与质量
后端测试：
```
mvn test
```
前端测试（如有配置）：
```
cd frontend
npm test
```
工程规范与质量要求：project_document/development_standards.md

## 9. 架构与模块
- 端到端架构说明：project_document/architecture.md
- 领域模块与社区能力：project_document/poem_community_implementation.md、project_document/user-creations-improvement.md
- 常见功能修复与变更记录：
  - project_document/comment_fix_summary.md
  - project_document/writer_modal_fix_summary.md
  - project_document/fix_favorite_button_event_bubble.md
  - project_document/search_fix_summary.md

## 10. 部署
- 参考：project_document/deployment_guide.md
- 通常流程：
  1) 设置环境变量与安全配置（JWT、数据库等）
  2) 后端构建并运行 jar
  3) 前端打包并通过 Web 服务器托管构建产物

## 11. 贡献指南
- 请遵循项目代码规范与提交信息要求：project_document/development_standards.md
- 建议在提交前本地运行：构建、测试、lint（前端）
- 如涉及接口变更，请同步更新：project_document/api_documentation.md

## 12. 许可证
- 本仓库未明确提供开源许可证。如需开源或对外分发，请先在内部评审后添加适当的 LICENSE 文件。

---

如需更多帮助，请阅读 project_document/main.md 获取项目总览。
