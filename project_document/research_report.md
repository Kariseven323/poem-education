# 诗词交流鉴赏平台 - 技术研究报告

## 1. 技术选型分析

### 1.1 后端框架选择：Spring Boot

#### 选择理由
- **成熟稳定**: Spring生态系统完善，社区活跃
- **开发效率**: 约定优于配置，快速开发
- **微服务支持**: 便于后期扩展为微服务架构
- **丰富的集成**: 与各种中间件集成良好

#### 版本选择
- **Spring Boot 2.7.x**: 稳定版本，LTS支持
- **Spring Framework 5.3.x**: 对应的Spring版本
- **Java 11**: LTS版本，性能和稳定性兼顾

### 1.2 数据存储方案：多数据源架构

#### MySQL - 关系型数据存储
**适用场景**:
- 用户基础信息
- 权限管理数据
- 统计数据
- 系统配置

**优势**:
- ACID事务保证
- 成熟的查询优化
- 丰富的工具生态
- 数据一致性强

#### MongoDB - 文档型数据存储
**适用场景**:
- 诗词内容存储
- 评论数据（支持嵌套结构）
- 用户创作作品
- AI分析结果

**优势**:
- 灵活的文档结构
- 天然支持JSON格式
- 水平扩展能力强
- 适合内容管理

#### Redis - 缓存和会话存储
**适用场景**:
- 用户会话管理
- 热点数据缓存
- 搜索结果缓存
- 计数器和统计

**优势**:
- 高性能读写
- 丰富的数据结构
- 持久化支持
- 分布式锁支持

### 1.3 架构模式选择：单模块架构

#### 选择理由
- **项目初期**: 功能相对集中，复杂度可控
- **开发效率**: 减少分布式复杂性
- **部署简单**: 单一部署单元，运维成本低
- **性能优势**: 避免网络调用开销

#### 扩展策略
- **垂直扩展**: 增加服务器配置
- **读写分离**: 数据库层面优化
- **缓存优化**: 多级缓存策略
- **后期拆分**: 按业务域拆分微服务

## 2. 核心功能技术方案

### 2.1 用户认证与授权

#### JWT Token方案
```java
// JWT配置
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
}
```

#### Spring Security集成
- 基于角色的访问控制(RBAC)
- 方法级别的权限控制
- 跨域请求处理

### 2.2 层级评论系统

#### 数据结构设计
```javascript
// MongoDB评论文档结构
{
  "_id": ObjectId,
  "targetId": NumberLong,
  "userId": NumberLong,
  "content": "评论内容",
  "parentId": ObjectId,
  "path": "1.2.3",      // 评论路径，便于查询
  "level": NumberInt,    // 评论层级
  "children": []         // 子评论数组（可选）
}
```

#### 查询优化策略
- 使用path字段实现高效的层级查询
- 分页加载，避免深度递归
- 缓存热门评论

### 2.3 AI评分机制

#### 评分维度设计
```java
public class PoemScoreDTO {
    private Integer totalScore;     // 总分
    private Integer rhythm;         // 韵律得分
    private Integer imagery;        // 意象得分
    private Integer emotion;        // 情感得分
    private Integer technique;      // 技法得分
    private Integer innovation;     // 创新得分
    private String feedback;        // AI反馈
}
```

#### 雷达图生成
- 使用Chart.js生成前端雷达图
- 后端提供标准化的数据格式
- 支持多维度对比分析

### 2.4 搜索功能实现

#### 多字段搜索策略
```java
// MongoDB全文搜索
@Query("{ $text: { $search: ?0 } }")
List<Poem> findByFullText(String keyword);

// 复合查询
@Query("{ $and: [ " +
       "{ $or: [ " +
       "  { 'title': { $regex: ?0, $options: 'i' } }, " +
       "  { 'content.original': { $regex: ?0, $options: 'i' } }, " +
       "  { 'author': { $regex: ?0, $options: 'i' } } " +
       "] }, " +
       "{ 'dynasty': ?1 } " +
       "] }")
List<Poem> findByKeywordAndDynasty(String keyword, String dynasty);
```

#### 搜索优化
- 建立复合索引
- 搜索结果缓存
- 搜索建议功能

## 3. 性能优化策略

### 3.1 数据库优化
- **索引策略**: 根据查询模式建立合适索引
- **分页查询**: 使用游标分页避免深分页问题
- **连接池**: 合理配置数据库连接池
- **读写分离**: 主从复制，读写分离

### 3.2 缓存策略
- **多级缓存**: 本地缓存 + Redis缓存
- **缓存预热**: 系统启动时预加载热点数据
- **缓存更新**: 采用Cache-Aside模式
- **缓存穿透**: 布隆过滤器防护

### 3.3 应用层优化
- **异步处理**: 使用@Async处理耗时操作
- **批量操作**: 减少数据库交互次数
- **对象池**: 复用重对象，减少GC压力
- **压缩传输**: 启用Gzip压缩

## 4. 安全策略

### 4.1 数据安全
- **密码加密**: BCrypt加密存储
- **敏感信息**: 配置文件加密
- **SQL注入**: 使用参数化查询
- **XSS防护**: 输入输出过滤

### 4.2 接口安全
- **访问限制**: IP白名单和黑名单
- **请求限流**: 基于用户和IP的限流
- **参数校验**: 严格的输入验证
- **HTTPS**: 强制使用HTTPS传输

## 5. 监控与运维

### 5.1 应用监控
- **健康检查**: Spring Boot Actuator
- **性能监控**: Micrometer + Prometheus
- **日志管理**: Logback + ELK Stack
- **链路追踪**: Spring Cloud Sleuth

### 5.2 数据库监控
- **慢查询**: 监控和优化慢SQL
- **连接数**: 监控数据库连接使用情况
- **锁等待**: 监控数据库锁竞争
- **备份策略**: 定期备份和恢复测试
