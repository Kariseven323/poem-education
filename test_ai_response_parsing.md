# AI响应解析测试

## 问题描述
AI返回的响应包含`<think>`标签的思考过程，导致后端JSON解析失败。

## 测试用例

### 1. 带思考过程的AI响应
```
<think>
嗯，用户让我对一首题为《暮色》的绝句进行专业诗词评价，并用JSON格式给出五个维度的分数和详细分析。看来ta需要一份结构化、标准化的文学赏析报告。

这首诗描绘的是傍晚时分溪边的景象，整体意境很宁静优美。"残阳浸在溪水里"这个意象特别打动人，把夕阳余晖比作浸泡在溪水中，既有色彩感又带着湿润的画面质感。
</think>

{
  "totalScore": 85,
  "dimensions": {
    "rhythm": 80,
    "imagery": 85,
    "emotion": 90,
    "technique": 75,
    "innovation": 85
  },
  "details": "这是一首优美的绝句作品..."
}
```

### 2. 纯JSON响应
```json
{
  "totalScore": 85,
  "dimensions": {
    "rhythm": 80,
    "imagery": 85,
    "emotion": 90,
    "technique": 75,
    "innovation": 85
  },
  "details": "这是一首优美的绝句作品..."
}
```

## 解决方案

### 后端修改
1. **扩展数据模型**：在`Creation.AiScore`中添加`thinkingProcess`字段
2. **智能解析**：添加`extractAIResponseData`方法，支持多种AI响应格式
3. **JSON提取**：使用`extractJsonObject`方法提取纯JSON部分

### 前端修改
1. **导入组件**：添加`Collapse`和`BulbOutlined`
2. **思考过程显示**：在AI评分详情中添加可折叠的思考过程展示

## 预期效果
- 后端能够正确解析包含思考过程的AI响应
- 前端显示AI思考过程，提升用户体验
- 保持向后兼容性，支持纯JSON格式的响应
