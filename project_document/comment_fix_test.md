# 评论提交修复测试计划

## 修复内容总结

### 1. 问题根源
- MongoDB schema要求 `targetId` 为 `bsonType: "objectId"`
- Java代码直接传递字符串，未进行ObjectId转换

### 2. 修复方案
在 `CommentServiceImpl.createComment()` 方法中添加：
- 导入 `org.bson.types.ObjectId`
- 新增 `validateAndConvertObjectId()` 方法
- 在保存前验证并转换 `targetId` 和 `parentId`

### 3. 修复代码位置
- **文件**: `src/main/java/com/poem/education/service/impl/CommentServiceImpl.java`
- **行数**: 33行（导入）、91-96行（转换逻辑）、337-352行（验证方法）

## 测试步骤

### 前置条件
1. 确保MongoDB服务运行
2. 确保Spring Boot应用启动
3. 确保前端应用运行在 http://localhost:3000

### 测试用例1: 正常评论提交
**请求数据**:
```json
{
  "targetId": "5b9a0254367d5caccce1aa13",
  "targetType": "guwen", 
  "content": "这是一条测试评论"
}
```

**预期结果**: 
- HTTP 200 OK
- 返回评论详情
- MongoDB中成功插入记录

### 测试用例2: 回复评论提交
**请求数据**:
```json
{
  "targetId": "5b9a0254367d5caccce1aa13",
  "targetType": "guwen",
  "content": "这是一条回复评论", 
  "parentId": "[已存在的评论ID]"
}
```

**预期结果**:
- HTTP 200 OK
- 正确设置层级和路径
- 父评论回复数+1

### 测试用例3: 无效ObjectId格式
**请求数据**:
```json
{
  "targetId": "invalid_id",
  "targetType": "guwen",
  "content": "测试无效ID"
}
```

**预期结果**:
- HTTP 400 Bad Request
- 错误信息: "目标ID格式无效，必须是24位十六进制字符串"

## 验证方法

### 1. 前端测试
- 打开诗词详情页面
- 输入评论内容
- 点击"发表评论"按钮
- 检查是否成功提交

### 2. 后端日志检查
```bash
# 查看应用日志
tail -f logs/application.log | grep "发表评论"
```

### 3. MongoDB数据验证
```javascript
// 连接MongoDB
use poem_education

// 查看最新评论
db.comments.find().sort({createdAt: -1}).limit(5)

// 验证targetId类型
db.comments.findOne({}, {targetId: 1, _id: 0})
```

## 回滚方案
如果修复出现问题，可以：
1. 恢复 `CommentServiceImpl.java` 到修改前版本
2. 重启Spring Boot应用
3. 检查是否恢复到原始错误状态
