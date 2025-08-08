# 评论回复功能说明

## 功能概述

在诗词详细信息窗口中新增了评论回复功能，用户可以：

1. **点击回复按钮**：在每条评论下方点击"回复"按钮
2. **弹出回复框**：直接在评论下方显示回复输入框
3. **主评论区引用**：在主评论输入框中显示引用信息

## 功能特性

### 1. 两种回复方式

#### 方式一：评论下方直接回复
- 点击评论下的"回复"按钮
- 在评论下方弹出回复输入框
- 显示引用的原评论内容
- 可以取消回复

#### 方式二：主评论区回复
- 点击"回复"按钮后，主评论输入框会显示引用信息
- 引用框显示被回复用户和评论内容
- 可以点击关闭按钮取消回复

### 2. 用户体验优化

- **引用显示**：清晰显示回复的目标评论和用户
- **状态管理**：智能管理回复状态，避免冲突
- **视觉反馈**：不同的颜色和样式区分回复和普通评论
- **动画效果**：平滑的显示/隐藏动画

### 3. 数据结构支持

- 支持 `parentId` 字段，建立评论层级关系
- 后端已支持层级评论查询
- 前端正确处理回复数据提交

## 技术实现

### 状态管理
```javascript
const [replyingTo, setReplyingTo] = useState(null); // 当前回复的评论
const [replyContent, setReplyContent] = useState(''); // 回复内容
const [replyVisible, setReplyVisible] = useState({}); // 控制回复框显示
```

### 核心函数
- `handleReplyClick(comment)`: 处理回复按钮点击
- `handleSubmitReply(commentId)`: 提交回复
- `handleCancelReply()`: 取消回复

### API 调用
```javascript
// 创建回复评论
const response = await commentAPI.create({
  targetId: poemId,
  targetType: 'guwen',
  content: replyContent.trim(),
  parentId: commentId  // 父评论ID
});
```

## 样式说明

### CSS 类名
- `.reply-input-section`: 回复输入区域
- `.reply-reference`: 回复引用框
- `.reply-reference-main`: 主评论区引用框
- `.reply-actions`: 回复操作按钮组

### 颜色方案
- 引用框背景：`#f0f8ff` (浅蓝色)
- 引用框边框：`#d6e4ff` (蓝色)
- 左侧标识线：`#1890ff` (主题蓝)

## 使用流程

1. **用户查看评论**：在诗词详情页面查看评论列表
2. **点击回复**：点击想要回复的评论下方的"回复"按钮
3. **选择回复方式**：
   - 直接在评论下方的回复框中输入
   - 或在主评论输入框中输入（会显示引用信息）
4. **提交回复**：点击"发送"或"发表回复"按钮
5. **查看结果**：回复成功后，评论列表会自动刷新

## 注意事项

- 用户必须登录才能回复评论
- 回复内容不能为空
- 支持取消回复操作
- 回复成功后会自动清空输入框并刷新评论列表
