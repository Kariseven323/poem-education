# 诗词交流鉴赏平台 - 数据库迁移指南

## 1. 迁移概述

### 1.1 现有数据结构
项目已包含完整的MongoDB诗词数据库：
- **guwen集合**: 古文诗词数据（约10万+条记录）
- **sentences集合**: 诗句数据（约1万条记录）
- **writers集合**: 作者信息数据

### 1.2 迁移目标
- 保留现有MongoDB数据不变
- 新增MySQL数据库存储用户和行为数据
- 建立跨数据库的关联关系
- 实现数据同步机制

## 2. 迁移步骤

### 2.1 第一阶段：MySQL数据库初始化

#### 执行MySQL初始化脚本
```bash
# 1. 连接MySQL
mysql -u root -p

# 2. 执行初始化脚本
source database/mysql_init.sql

# 3. 验证表创建
USE poem_education;
SHOW TABLES;
```

#### 验证MySQL表结构
```sql
-- 检查核心表
DESCRIBE users;
DESCRIBE user_actions;
DESCRIBE content_stats;

-- 检查索引
SHOW INDEX FROM users;
SHOW INDEX FROM user_actions;
```

### 2.2 第二阶段：MongoDB扩展

#### 执行MongoDB扩展脚本
```bash
# 1. 连接MongoDB
mongo

# 2. 执行扩展脚本
load("database/mongodb_extend.js")

# 3. 验证新集合
use poem_education
show collections
```

#### 验证MongoDB扩展结果
```javascript
// 检查新集合
db.comments.getIndexes()
db.creations.getIndexes()

// 检查视图
db.runCommand({listCollections: 1, filter: {type: "view"}})

// 测试聚合查询
db.hot_guwen.findOne()
```

### 2.3 第三阶段：数据同步机制

#### 创建数据同步脚本
```python
# sync_stats.py - 统计数据同步脚本
import pymongo
import mysql.connector
from datetime import datetime

class DataSyncService:
    def __init__(self):
        # MongoDB连接
        self.mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
        self.mongo_db = self.mongo_client["poem_education"]
        
        # MySQL连接
        self.mysql_conn = mysql.connector.connect(
            host="localhost",
            user="poem_user",
            password="your_password",
            database="poem_education"
        )
        self.mysql_cursor = self.mysql_conn.cursor()
    
    def sync_content_stats(self):
        """同步内容统计数据"""
        # 从MySQL获取统计数据
        query = """
        SELECT content_id, content_type, view_count, like_count, 
               favorite_count, comment_count, share_count
        FROM content_stats
        """
        self.mysql_cursor.execute(query)
        stats = self.mysql_cursor.fetchall()
        
        # 更新MongoDB中的统计信息（如果需要）
        for stat in stats:
            content_id, content_type, view_count, like_count, favorite_count, comment_count, share_count = stat
            # 这里可以添加更新MongoDB的逻辑
            pass
    
    def sync_user_actions(self):
        """同步用户行为数据"""
        # 实现用户行为数据同步逻辑
        pass
```

## 3. 数据关联策略

### 3.1 跨数据库关联设计

#### MongoDB ObjectId与MySQL关联
```yaml
关联方式:
  - MySQL中使用VARCHAR(100)存储MongoDB的ObjectId字符串
  - 应用层负责ID转换和关联查询
  - 使用Redis缓存关联结果

示例关联:
  user_actions.target_id → guwen._id.toString()
  user_favorites.target_id → guwen._id.toString()
  comments.targetId → guwen._id (ObjectId)
```

#### 应用层关联查询示例
```java
@Service
public class PoemService {
    
    @Autowired
    private GuwenRepository guwenRepository;
    
    @Autowired
    private ContentStatsRepository contentStatsRepository;
    
    public PoemDetailDTO getPoemDetail(String objectId) {
        // 1. 从MongoDB获取古文内容
        Guwen guwen = guwenRepository.findById(new ObjectId(objectId));
        
        // 2. 从MySQL获取统计信息
        ContentStats stats = contentStatsRepository.findByContentIdAndContentType(
            objectId, "guwen");
        
        // 3. 组装返回结果
        return PoemDetailDTO.builder()
            .guwen(guwen)
            .stats(stats)
            .build();
    }
}
```

### 3.2 数据一致性保证

#### 最终一致性策略
```yaml
策略选择:
  - 读操作: 优先从缓存读取，缓存失效时查询数据库
  - 写操作: 先写主数据库，异步更新关联数据
  - 同步机制: 定时任务 + 消息队列

实现方案:
  1. 用户行为写入MySQL
  2. 异步更新MongoDB统计字段
  3. 定时任务校验数据一致性
  4. Redis缓存热点数据
```

#### 数据同步定时任务
```java
@Component
public class DataSyncScheduler {
    
    @Scheduled(fixedRate = 300000) // 5分钟执行一次
    public void syncContentStats() {
        // 同步内容统计数据
        List<UserAction> recentActions = userActionRepository
            .findByCreatedAtAfter(LocalDateTime.now().minusMinutes(5));
        
        // 批量更新统计数据
        Map<String, Integer> statsMap = new HashMap<>();
        for (UserAction action : recentActions) {
            String key = action.getTargetId() + "_" + action.getActionType();
            statsMap.merge(key, 1, Integer::sum);
        }
        
        // 更新content_stats表
        batchUpdateContentStats(statsMap);
    }
}
```

## 4. 性能优化

### 4.1 查询优化策略

#### MongoDB查询优化
```javascript
// 使用复合索引优化查询
db.guwen.find({dynasty: "唐", writer: "李白"}).hint({dynasty_1_writer_1: 1})

// 使用聚合管道优化关联查询
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

#### MySQL查询优化
```sql
-- 使用覆盖索引
SELECT content_id, view_count, like_count 
FROM content_stats 
WHERE content_type = 'guwen' 
ORDER BY view_count DESC 
LIMIT 10;

-- 分区表查询
SELECT * FROM user_actions 
WHERE created_at >= '2025-01-01' 
  AND target_type = 'guwen';
```

### 4.2 缓存策略

#### Redis缓存设计
```yaml
缓存层级:
  L1: 应用内存缓存 (Caffeine)
  L2: Redis分布式缓存
  L3: 数据库

缓存模式:
  - 热点数据: Cache-Aside
  - 统计数据: Write-Behind
  - 搜索结果: Cache-Aside + TTL
```

## 5. 监控与维护

### 5.1 数据一致性监控

#### 监控脚本
```python
def check_data_consistency():
    """检查数据一致性"""
    # 检查MySQL和MongoDB的数据一致性
    mysql_count = get_mysql_content_count()
    mongo_count = get_mongo_content_count()
    
    if abs(mysql_count - mongo_count) > 100:
        send_alert("数据一致性异常")
    
    # 检查统计数据准确性
    check_stats_accuracy()
```

### 5.2 性能监控

#### 关键指标
```yaml
数据库性能:
  - 查询响应时间
  - 连接池使用率
  - 慢查询统计
  - 索引使用率

应用性能:
  - 缓存命中率
  - 接口响应时间
  - 错误率统计
  - 并发用户数
```

## 6. 故障恢复

### 6.1 备份策略
```bash
# MongoDB备份
mongodump --db poem_education --out /backup/mongodb/

# MySQL备份
mysqldump -u poem_user -p poem_education > /backup/mysql/poem_education.sql

# Redis备份
redis-cli BGSAVE
```

### 6.2 恢复流程
```bash
# MongoDB恢复
mongorestore --db poem_education /backup/mongodb/poem_education/

# MySQL恢复
mysql -u poem_user -p poem_education < /backup/mysql/poem_education.sql

# 数据同步恢复
python sync_stats.py --full-sync
```

## 7. 迁移验证

### 7.1 功能验证清单
- [ ] 用户注册登录功能
- [ ] 古文浏览和搜索功能
- [ ] 评论发表和查看功能
- [ ] 点赞收藏功能
- [ ] 统计数据准确性
- [ ] 性能指标达标

### 7.2 数据验证脚本
```python
def validate_migration():
    """验证迁移结果"""
    # 验证数据完整性
    assert mongo_db.guwen.count_documents({}) > 0
    assert mysql_cursor.execute("SELECT COUNT(*) FROM users").fetchone()[0] >= 0
    
    # 验证关联关系
    validate_cross_database_relations()
    
    # 验证索引效果
    validate_query_performance()
    
    print("迁移验证通过！")
```

迁移完成后，系统将具备完整的多数据源架构，既保留了现有的丰富诗词数据，又具备了现代化的用户管理和交互功能。
