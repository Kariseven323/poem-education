# 诗词交流鉴赏平台 - 数据库设计文档

## 1. 数据库架构概览

### 1.1 多数据源策略
- **MySQL**: 存储结构化数据（用户、权限、统计、关系数据等）
- **MongoDB**: 存储文档数据（诗词内容、评论、创作等）- **基于现有数据结构**
- **Redis**: 缓存热点数据和会话信息

### 1.2 数据分布原则
- **关系型数据** → MySQL（用户管理、权限、统计、行为数据）
- **内容型数据** → MongoDB（诗词内容、评论、创作作品）
- **临时性数据** → Redis（会话、缓存、计数器）

### 1.3 现有MongoDB数据结构
项目已包含完整的诗词数据库，包含：
- **guwen集合**: 古文诗词数据（约10万+条记录）
- **sentences集合**: 诗句数据（约1万条记录）
- **writers集合**: 作者信息数据

## 2. MySQL数据库设计

### 2.1 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    bio TEXT,
    status TINYINT DEFAULT 1 COMMENT '1:正常 0:禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

### 2.2 用户角色表 (user_roles)
```sql
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_role (user_id, role_name)
);
```

### 2.3 用户行为表 (user_actions)
```sql
CREATE TABLE user_actions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(100) NOT NULL COMMENT 'MongoDB ObjectId或其他标识',
    target_type VARCHAR(50) NOT NULL COMMENT 'guwen/creation/comment/sentence',
    action_type VARCHAR(50) NOT NULL COMMENT 'like/favorite/view/share',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_target_action (user_id, target_id, target_type, action_type),
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_id, target_type),
    INDEX idx_action_type (action_type)
);
```

### 2.4 用户收藏表 (user_favorites)
```sql
CREATE TABLE user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(100) NOT NULL COMMENT 'MongoDB ObjectId',
    target_type VARCHAR(50) NOT NULL COMMENT 'guwen/sentence/writer',
    folder_name VARCHAR(100) DEFAULT '默认收藏夹',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_favorite (user_id, target_id, target_type),
    INDEX idx_user_folder (user_id, folder_name)
);
```

### 2.5 学习记录表 (learning_records)
```sql
CREATE TABLE learning_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(100) NOT NULL COMMENT 'MongoDB ObjectId',
    target_type VARCHAR(50) NOT NULL COMMENT 'guwen/sentence',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    progress_status TINYINT DEFAULT 0 COMMENT '0:未开始 1:学习中 2:已完成',
    last_position TEXT COMMENT '学习位置记录',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_target (user_id, target_id, target_type),
    INDEX idx_user_progress (user_id, progress_status)
);
```

## 3. MongoDB集合设计（基于现有数据结构）

### 3.1 古文集合 (guwen) - **现有数据**
```javascript
{
  "_id": ObjectId("..."),
  "title": "诗词标题",
  "dynasty": "朝代",
  "writer": "作者",
  "content": "诗词内容",
  "type": ["标签1", "标签2"],
  "remark": "注释",
  "shangxi": "赏析",
  "translation": "翻译",
  "audioUrl": "音频链接"
}
```

**现有索引**:
- `title_1`: 标题索引
- `writer_1`: 作者索引
- `dynasty_1`: 朝代索引
- `title_1_writer_1`: 复合索引

### 3.2 句子集合 (sentences) - **现有数据**
```javascript
{
  "_id": ObjectId("..."),
  "name": "诗句内容",
  "from": "出处"
}
```

**现有索引**:
- `name_1`: 句子内容索引
- `from_1`: 出处索引

### 3.3 作者集合 (writers) - **现有数据**
```javascript
{
  "_id": ObjectId("..."),
  "name": "作者姓名",
  "headImageUrl": "头像链接",
  "simpleIntro": "简介",
  "detailIntro": "详细介绍(JSON字符串)"
}
```

**现有索引**:
- `name_1`: 作者姓名唯一索引

### 3.4 评论集合 (comments) - **新增**
```javascript
{
  "_id": ObjectId,
  "targetId": ObjectId,     // 目标文档ID（guwen._id等）
  "targetType": "guwen|creation|sentence",
  "userId": NumberLong,     // 对应MySQL users.id
  "content": "评论内容",
  "parentId": ObjectId,     // 父评论ID，支持嵌套
  "level": NumberInt,       // 评论层级
  "path": "1.2.3",         // 评论路径，便于查询
  "likeCount": NumberInt,
  "replyCount": NumberInt,
  "status": NumberInt,      // 1:正常 0:删除
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

**建议索引**:
- `targetId_1_targetType_1`: 目标复合索引
- `userId_1`: 用户索引
- `parentId_1`: 父评论索引
- `path_1`: 路径索引

### 3.5 用户创作集合 (creations) - **新增**
```javascript
{
  "_id": ObjectId,
  "userId": NumberLong,     // 对应MySQL users.id
  "title": "作品标题",
  "content": "作品内容",
  "style": "创作风格",      // 如：律诗、绝句、词、散文等
  "description": "作品描述",
  "aiScore": {
    "totalScore": NumberInt,
    "dimensions": {
      "rhythm": NumberInt,      // 韵律
      "imagery": NumberInt,     // 意象
      "emotion": NumberInt,     // 情感
      "technique": NumberInt,   // 技法
      "innovation": NumberInt   // 创新
    },
    "feedback": "AI反馈建议",
    "scoredAt": ISODate
  },
  "radarData": {
    "labels": ["韵律", "意象", "情感", "技法", "创新"],
    "values": [80, 75, 90, 70, 85]
  },
  "status": NumberInt,      // 1:公开 0:私有 -1:删除
  "viewCount": NumberInt,
  "likeCount": NumberInt,
  "commentCount": NumberInt,
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

**建议索引**:
- `userId_1`: 用户索引
- `status_1`: 状态索引
- `createdAt_-1`: 创建时间倒序索引
- `aiScore.totalScore_-1`: 评分排序索引

## 4. Redis缓存设计

### 4.1 缓存键命名规范
```yaml
# 用户相关
session:user:{userId}              # 用户会话信息
profile:user:{userId}              # 用户资料缓存
favorites:user:{userId}            # 用户收藏列表

# 诗词相关
hot:guwen:daily                    # 每日热门古文
hot:guwen:weekly                   # 每周热门古文
search:guwen:{hash}                # 古文搜索结果缓存
detail:guwen:{objectId}            # 古文详情缓存

# 作者相关
detail:writer:{objectId}           # 作者详情缓存
works:writer:{objectId}            # 作者作品列表

# 句子相关
random:sentences:daily             # 每日推荐句子
search:sentences:{hash}            # 句子搜索结果

# 统计相关
stats:user:{userId}                # 用户统计数据
stats:guwen:{objectId}             # 古文统计数据
stats:global:daily                 # 全局每日统计

# 排行榜
rank:guwen:views:daily             # 古文浏览排行榜
rank:guwen:likes:weekly            # 古文点赞排行榜
rank:users:active:monthly          # 用户活跃排行榜
```

### 4.2 缓存策略详细设计

#### 4.2.1 数据缓存策略
```yaml
# 热点数据缓存
热门古文列表:
  key: hot:guwen:daily
  ttl: 1小时
  更新: 定时任务每小时更新

古文详情:
  key: detail:guwen:{objectId}
  ttl: 30分钟
  更新: 查询时缓存，修改时删除

搜索结果:
  key: search:guwen:{hash}
  ttl: 10分钟
  更新: 搜索时缓存

用户会话:
  key: session:user:{userId}
  ttl: 24小时
  更新: 登录时创建，活动时延期
```

#### 4.2.2 计数器缓存
```yaml
# 实时计数器
浏览计数:
  key: counter:view:guwen:{objectId}
  ttl: 永久
  更新: 每次浏览+1，定时同步到MongoDB

点赞计数:
  key: counter:like:guwen:{objectId}
  ttl: 永久
  更新: 点赞/取消点赞时更新

用户活跃度:
  key: counter:active:user:{userId}
  ttl: 24小时
  更新: 用户操作时+1
```

### 4.3 缓存更新策略
- **Cache-Aside模式**: 应用程序负责缓存的读写
- **Write-Through模式**: 写入数据库同时更新缓存
- **Write-Behind模式**: 异步批量写入数据库
- **缓存预热**: 系统启动时预加载热点数据

### 4.4 缓存失效策略
- **TTL过期**: 根据数据特性设置合理过期时间
- **主动删除**: 数据更新时主动删除相关缓存
- **版本控制**: 使用版本号避免缓存不一致
- **降级策略**: 缓存失效时直接查询数据库

## 5. 数据库关系设计

### 5.1 跨数据库关联关系
```yaml
MySQL → MongoDB关联:
  user_actions.target_id → guwen._id (ObjectId转字符串)
  user_favorites.target_id → guwen._id/sentences._id/writers._id
  learning_records.target_id → guwen._id/sentences._id

MongoDB → MySQL关联:
  comments.userId → users.id
  creations.userId → users.id
```

### 5.2 数据一致性保证
- **最终一致性**: 允许短暂的数据不一致
- **补偿机制**: 定时任务检查和修复数据不一致
- **事务边界**: 在单个数据源内保证ACID特性
- **分布式锁**: 使用Redis实现跨数据源的并发控制

## 6. 性能优化策略

### 6.1 MongoDB优化
```javascript
// 复合索引优化
db.guwen.createIndex({dynasty: 1, writer: 1, title: 1})
db.comments.createIndex({targetId: 1, targetType: 1, createdAt: -1})

// 聚合查询优化
db.guwen.aggregate([
  {$match: {dynasty: "唐"}},
  {$lookup: {
    from: "writers",
    localField: "writer",
    foreignField: "name",
    as: "writerInfo"
  }},
  {$limit: 20}
])
```

### 6.2 MySQL优化
```sql
-- 分区表设计（按时间分区）
CREATE TABLE user_actions (
    -- 字段定义
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 读写分离配置
-- 主库：写操作
-- 从库：读操作
```

### 6.3 缓存优化
- **多级缓存**: 本地缓存 + Redis缓存
- **缓存预热**: 系统启动时预加载热点数据
- **缓存穿透防护**: 布隆过滤器 + 空值缓存
- **缓存雪崩防护**: 随机TTL + 熔断机制
