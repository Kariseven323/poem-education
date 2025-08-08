# 诗词社区API调用问题修复

## 问题描述

用户的诗词已经发布到社区（数据库中 `isPublic: true`），但诗词社区页面仍然显示"暂无诗词"。

## 根本原因

**前端API调用逻辑错误**：

1. **错误的API调用**：
   - `PoemCommunity.js` 在没有关键词时调用 `searchCreations('', page, pageSize, style)`
   - `searchCreations` 函数调用的是 `/creations/search` API
   - 但搜索API要求 `keyword` 参数不能为空

2. **API参数验证**：
   ```java
   @GetMapping("/search")
   public Result<PageResult<CreationDTO>> searchCreations(
       @RequestParam String keyword,  // 必需参数，不能为空
       // ...
   )
   ```

3. **前端逻辑问题**：
   ```javascript
   // 错误的逻辑
   if (keyword.trim()) {
       response = await searchCreations(keyword, page, pageSize, style);
   } else {
       // 这里仍然调用搜索API，但传递空字符串
       response = await searchCreations('', page, pageSize, style);
   }
   ```

## 修复方案

### 修改前端API调用逻辑

**文件**: `frontend/src/components/PoemCommunity.js`

**修改前**:
```javascript
// 获取公开创作
if (keyword.trim()) {
    response = await searchCreations(keyword, page, pageSize, style);
} else {
    // 错误：仍然调用搜索API
    response = await searchCreations('', page, pageSize, style);
}
```

**修改后**:
```javascript
// 获取公开创作
if (keyword.trim()) {
    // 有关键词时使用搜索API
    response = await searchCreations(keyword, page, pageSize, style);
} else {
    // 无关键词时使用公开创作列表API
    const params = { page, size: pageSize };
    if (style) params.style = style;
    response = await creationAPI.getPublicList(params);
}
```

### 添加必要的导入

```javascript
import { searchCreations, getUserCreations, toggleLike, creationAPI } from '../utils/api';
```

## API端点对比

### 1. 搜索API (需要关键词)
```
GET /api/v1/creations/search?keyword=春天&page=1&size=20&style=律诗
```
- **用途**: 根据关键词搜索公开创作
- **参数**: `keyword` (必需), `page`, `size`, `style`
- **后端方法**: `CreationServiceImpl.searchCreations()`

### 2. 公开创作列表API (不需要关键词)
```
GET /api/v1/creations/public?page=1&size=20&style=律诗
```
- **用途**: 获取所有公开创作列表
- **参数**: `page`, `size`, `style` (都是可选)
- **后端方法**: `CreationServiceImpl.getPublicCreations()`

## 数据流程

### 修复前的错误流程
```
前端: 无关键词 → searchCreations('') → /creations/search?keyword= → 后端参数验证失败 → 返回错误
```

### 修复后的正确流程
```
前端: 无关键词 → creationAPI.getPublicList() → /creations/public → 后端查询 isPublic=true → 返回公开创作列表
```

## 后端查询逻辑

修复后的后端查询确保只返回公开的创作：

```java
// CreationServiceImpl.getPublicCreations()
if (style != null && !style.trim().isEmpty()) {
    // 按风格和公开状态查询
    creationPage = creationRepository.findByStyleAndStatusAndIsPublic(style, 1, true, pageable);
} else {
    // 只按状态和公开状态查询
    creationPage = creationRepository.findByStatusAndIsPublic(1, true, pageable);
}
```

## 测试验证

### 测试步骤

1. **清除浏览器缓存**
2. **访问诗词社区页面**
3. **检查网络请求**：
   - 应该看到 `GET /api/v1/creations/public?page=1&size=20`
   - 不应该看到 `GET /api/v1/creations/search?keyword=`

4. **验证响应数据**：
   - API应该返回包含您诗词的列表
   - 响应格式：
     ```json
     {
       "code": 200,
       "data": {
         "items": [
           {
             "id": "6895a16d8e12ce3e60382410",
             "title": "暮色",
             "content": "残阳浸在溪水里...",
             "isPublic": true,
             // ...
           }
         ],
         "total": 1
       }
     }
     ```

### 预期结果

- ✅ 诗词社区页面显示您的诗词《暮色》
- ✅ 可以看到诗词内容、作者、创建时间等信息
- ✅ 可以进行点赞、评论等操作
- ✅ 搜索功能正常工作

## 相关文件修改

1. **frontend/src/components/PoemCommunity.js**
   - 修改 `loadPoems` 函数的API调用逻辑
   - 添加 `creationAPI` 导入

2. **后端文件 (已修复)**
   - `CreationServiceImpl.java` - 查询逻辑已正确实现
   - `CreationRepository.java` - 查询方法已添加
   - `CreationController.java` - API端点已存在

## 总结

这个问题的根本原因是前端在获取公开创作列表时错误地调用了搜索API，而搜索API要求必须提供关键词参数。修复后，前端会根据是否有关键词来选择正确的API：

- **有关键词** → 调用搜索API (`/creations/search`)
- **无关键词** → 调用公开列表API (`/creations/public`)

现在您的诗词《暮色》应该能够正常在诗词社区中显示了！
