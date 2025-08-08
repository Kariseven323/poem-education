#!/bin/bash

# 评论API详细调试脚本
echo "=== 评论API详细调试 ==="

API_BASE="http://localhost:8080/api/v1"
VALID_OBJECT_ID="5b9a0254367d5caccce1aa13"

echo "1. 测试简单的评论创建..."
echo "请求数据: {\"targetId\": \"$VALID_OBJECT_ID\", \"targetType\": \"guwen\", \"content\": \"测试评论\"}"

# 使用更详细的curl输出
curl -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "{
    \"targetId\": \"$VALID_OBJECT_ID\",
    \"targetType\": \"guwen\",
    \"content\": \"测试评论内容\"
  }" \
  -w "\n\nHTTP状态码: %{http_code}\n响应时间: %{time_total}s\n" \
  -v

echo ""
echo "2. 测试最小化的JSON..."
curl -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d '{"targetId":"5b9a0254367d5caccce1aa13","targetType":"guwen","content":"test"}' \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo "3. 测试字段验证..."
curl -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d '{"targetId":"","targetType":"guwen","content":"test"}' \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo "4. 测试无效的targetType..."
curl -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d '{"targetId":"5b9a0254367d5caccce1aa13","targetType":"invalid","content":"test"}' \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo "5. 测试内容过长..."
LONG_CONTENT=$(printf 'a%.0s' {1..1001})
curl -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d "{\"targetId\":\"5b9a0254367d5caccce1aa13\",\"targetType\":\"guwen\",\"content\":\"$LONG_CONTENT\"}" \
  -w "\nHTTP状态码: %{http_code}\n"

echo ""
echo "调试完成！"
