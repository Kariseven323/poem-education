# 诗词社区显示问题修复

## 问题描述

用户将诗词状态修改为"已发布到社区"后，在诗词社区页面仍然显示"暂无诗词"。

## 根本原因

诗词社区页面的数据查询逻辑存在问题：

1. **`getPublicCreations` 方法**：只查询 `status=1` 的创作，没有检查 `isPublic=true`
2. **`searchCreations` 方法**：同样只查询 `status=1`，忽略了 `isPublic` 字段
3. **数据库查询方法缺失**：`CreationRepository` 中缺少支持 `isPublic` 字段的查询方法

## 修复方案

### 1. 扩展 CreationRepository 查询方法

**新增查询方法 (`src/main/java/com/poem/education/repository/mongodb/CreationRepository.java`):**

```java
// 基础公开状态查询
Page<Creation> findByStatusAndIsPublic(Integer status, Boolean isPublic, Pageable pageable);

// 风格 + 公开状态查询
Page<Creation> findByStyleAndStatusAndIsPublic(String style, Integer status, Boolean isPublic, Pageable pageable);

// 关键词搜索 + 公开状态
@Query("{ $and: [ " +
       "{ $or: [ " +
       "  { 'title': { $regex: ?0, $options: 'i' } }, " +
       "  { 'content': { $regex: ?0, $options: 'i' } } " +
       "] }, " +
       "{ 'status': ?1 }, " +
       "{ 'isPublic': ?2 } " +
       "] }")
Page<Creation> searchByKeywordAndIsPublic(String keyword, Integer status, Boolean isPublic, Pageable pageable);

// 关键词 + 风格 + 公开状态搜索
@Query("{ $and: [ " +
       "{ $or: [ " +
       "  { 'title': { $regex: ?0, $options: 'i' } }, " +
       "  { 'content': { $regex: ?0, $options: 'i' } } " +
       "] }, " +
       "{ 'style': ?1 }, " +
       "{ 'status': ?2 }, " +
       "{ 'isPublic': ?3 } " +
       "] }")
Page<Creation> searchByKeywordAndStyleAndIsPublic(String keyword, String style, Integer status, Boolean isPublic, Pageable pageable);
```

### 2. 修复 getPublicCreations 方法

**修改前：**
```java
// 只查询状态为1的创作
Page<Creation> creationPage = creationRepository.findByStatus(1, pageable);
```

**修改后：**
```java
Page<Creation> creationPage;
if (style != null && !style.trim().isEmpty()) {
    // 按风格和公开状态查询
    creationPage = creationRepository.findByStyleAndStatusAndIsPublic(style, 1, true, pageable);
} else {
    // 只按状态和公开状态查询
    creationPage = creationRepository.findByStatusAndIsPublic(1, true, pageable);
}
```

### 3. 修复 searchCreations 方法

**修改前：**
```java
// 只考虑关键词和风格，忽略公开状态
if (style != null && !style.trim().isEmpty()) {
    creationPage = creationRepository.searchByKeywordAndStyle(keyword, style, 1, pageable);
} else {
    creationPage = creationRepository.searchByKeyword(keyword, 1, pageable);
}
```

**修改后：**
```java
// 所有查询都加上公开状态检查
if (keyword != null && !keyword.trim().isEmpty()) {
    if (style != null && !style.trim().isEmpty()) {
        creationPage = creationRepository.searchByKeywordAndStyleAndIsPublic(keyword, style, 1, true, pageable);
    } else {
        creationPage = creationRepository.searchByKeywordAndIsPublic(keyword, 1, true, pageable);
    }
} else {
    if (style != null && !style.trim().isEmpty()) {
        creationPage = creationRepository.findByStyleAndStatusAndIsPublic(style, 1, true, pageable);
    } else {
        creationPage = creationRepository.findByStatusAndIsPublic(1, true, pageable);
    }
}
```

## 修复效果

### 数据查询逻辑

修复后的查询逻辑确保只显示满足以下条件的创作：
1. `status = 1` (正常状态)
2. `isPublic = true` (已发布到社区)

### API 端点影响

- **`GET /api/v1/creations/public`** - 获取公开创作列表
- **`GET /api/v1/creations/search`** - 搜索公开创作

### 前端页面影响

- **诗词社区页面** - 现在只显示已发布的公开创作
- **搜索功能** - 搜索结果只包含公开创作
- **风格筛选** - 筛选结果只包含公开创作

## 测试验证

### 测试步骤

1. **创建测试数据**：
   - 创建几首诗词，保持默认私有状态
   - 将其中一些发布到社区 (`isPublic = true`)

2. **验证社区页面**：
   - 访问诗词社区页面
   - 应该只看到已发布的诗词
   - 私有诗词不应该出现

3. **验证搜索功能**：
   - 搜索包含特定关键词的诗词
   - 结果应该只包含公开的诗词

4. **验证风格筛选**：
   - 按风格筛选诗词
   - 结果应该只包含该风格的公开诗词

### 预期结果

- ✅ 社区页面显示已发布的诗词
- ✅ 私有诗词不在社区中显示
- ✅ 搜索功能正常工作
- ✅ 风格筛选功能正常工作
- ✅ 发布/取消发布功能实时生效

## 数据库查询示例

### MongoDB 查询语句

```javascript
// 获取所有公开创作
db.creations.find({
  "status": 1,
  "isPublic": true
}).sort({"createdAt": -1})

// 按风格查询公开创作
db.creations.find({
  "status": 1,
  "isPublic": true,
  "style": "律诗"
}).sort({"createdAt": -1})

// 搜索公开创作
db.creations.find({
  "$and": [
    {
      "$or": [
        {"title": {"$regex": "春", "$options": "i"}},
        {"content": {"$regex": "春", "$options": "i"}}
      ]
    },
    {"status": 1},
    {"isPublic": true}
  ]
}).sort({"createdAt": -1})
```

## 相关文件修改

1. **CreationRepository.java** - 新增支持 `isPublic` 的查询方法
2. **CreationServiceImpl.java** - 修改 `getPublicCreations` 和 `searchCreations` 方法
3. **CreationServiceImpl.java** - 修复 `convertToDTO` 方法中的 `authorId` 映射问题

## 后续优化建议

1. **缓存优化**：为公开创作列表添加缓存
2. **索引优化**：为 `isPublic` 字段添加数据库索引
3. **分页优化**：优化大数据量下的分页查询性能
4. **实时更新**：考虑使用 WebSocket 实现实时更新社区内容
