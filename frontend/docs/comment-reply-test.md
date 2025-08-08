# 评论回复功能测试指南

## 问题修复说明

### 发现的问题
用户反馈：前端回复的内容父对象变成了诗词，查询后端数据库发现缺少回复的评论缺少 parentId 字段内容。

### 根本原因
在 `CommentServiceImpl.convertToDTO()` 方法中，使用了 `BeanUtils.copyProperties(comment, commentDTO)` 来复制属性，但是：
- `Comment` 实体中的 `parentId` 字段类型是 `ObjectId`
- `CommentDTO` 中的 `parentId` 字段类型是 `String`
- `BeanUtils.copyProperties` 无法正确转换 `ObjectId` 到 `String`，导致 `parentId` 字段丢失

### 修复方案
在 `convertToDTO` 方法中手动处理 ObjectId 字段的转换：

```java
// 手动处理 ObjectId 字段转换
if (comment.getTargetId() != null) {
    commentDTO.setTargetId(comment.getTargetId().toHexString());
}

if (comment.getParentId() != null) {
    commentDTO.setParentId(comment.getParentId().toHexString());
}
```

## 测试步骤

### 1. 前置条件
- 确保后端服务正常运行
- 确保前端应用正常运行
- 确保用户已登录

### 2. 测试用例

#### 测试用例1：发表普通评论
1. 打开任意诗词详情页面
2. 在评论输入框中输入评论内容
3. 点击"发表评论"按钮
4. 验证：评论成功发表，页面显示新评论

#### 测试用例2：回复评论（方式一：评论下方回复）
1. 在已有评论下方点击"回复"按钮
2. 验证：评论下方出现回复输入框，显示引用信息
3. 在回复框中输入回复内容
4. 点击"发送"按钮
5. 验证：回复成功发表，页面显示新回复

#### 测试用例3：回复评论（方式二：主评论区回复）
1. 点击任意评论的"回复"按钮
2. 验证：主评论输入框上方显示引用信息
3. 在主评论输入框中输入回复内容
4. 点击"发表回复"按钮
5. 验证：回复成功发表，页面显示新回复

#### 测试用例4：取消回复
1. 点击"回复"按钮激活回复状态
2. 点击引用框中的关闭按钮或"取消回复"按钮
3. 验证：回复状态被取消，界面恢复正常

### 3. 数据库验证

#### 验证回复评论的 parentId 字段
1. 发表一条回复评论
2. 在 MongoDB 中查询该评论记录
3. 验证：`parentId` 字段不为空，且值为被回复评论的 ObjectId

```javascript
// MongoDB 查询示例
db.comments.find({
  "targetType": "guwen",
  "parentId": { $ne: null }
}).sort({ "createdAt": -1 }).limit(5)
```

#### 验证评论层级结构
1. 查看回复评论的 `level` 字段
2. 验证：回复评论的 `level` 应该比父评论的 `level` 大 1

### 4. API 测试

#### 测试回复评论 API
```bash
curl -X POST http://localhost:8080/api/v1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "targetId": "POEM_OBJECT_ID",
    "targetType": "guwen",
    "content": "这是一条回复评论",
    "parentId": "PARENT_COMMENT_ID"
  }'
```

#### 验证响应数据
确认返回的评论数据包含正确的 `parentId` 字段：
```json
{
  "code": 200,
  "message": "发表评论成功",
  "data": {
    "_id": "NEW_COMMENT_ID",
    "targetId": "POEM_OBJECT_ID",
    "targetType": "guwen",
    "content": "这是一条回复评论",
    "parentId": "PARENT_COMMENT_ID",
    "level": 2,
    "userId": 123,
    "userInfo": {
      "nickname": "用户昵称",
      "avatar": "头像URL"
    },
    "likeCount": 0,
    "replyCount": 0,
    "status": 1,
    "createdAt": "2025-08-08T10:07:12",
    "updatedAt": "2025-08-08T10:07:12"
  }
}
```

## 预期结果

修复后，回复评论应该：
1. 正确保存 `parentId` 字段到数据库
2. 前端能正确显示回复关系
3. 评论层级结构正确
4. 用户体验流畅，无错误提示

## 注意事项

1. 确保 `parentId` 是有效的 24 位十六进制字符串
2. 被回复的评论必须存在
3. 回复层级不应超过系统限制（最大 10 层）
4. 用户必须登录才能发表回复
