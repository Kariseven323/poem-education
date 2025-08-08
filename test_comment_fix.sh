#!/bin/bash

# 评论提交修复测试脚本
# 用于验证ObjectId转换修复是否生效

echo "=== 评论提交修复测试 ==="

# 测试配置
API_BASE="http://localhost:3000/api/v1"
VALID_OBJECT_ID="5b9a0254367d5caccce1aa13"
INVALID_OBJECT_ID="invalid_id"

# 获取JWT Token (需要先登录)
echo "1. 获取测试用户Token..."
LOGIN_RESPONSE=$(curl -s -X POST "${API_BASE}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ 无法获取Token，请检查登录接口或用户凭据"
    exit 1
fi

echo "✅ Token获取成功: ${TOKEN:0:20}..."

# 测试用例1: 正常评论提交
echo ""
echo "2. 测试正常评论提交..."
COMMENT_RESPONSE=$(curl -s -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"targetId\": \"$VALID_OBJECT_ID\",
    \"targetType\": \"guwen\",
    \"content\": \"测试评论 - $(date)\"
  }")

echo "响应: $COMMENT_RESPONSE"

if echo "$COMMENT_RESPONSE" | grep -q '"code":200'; then
    echo "✅ 正常评论提交成功"
else
    echo "❌ 正常评论提交失败"
fi

# 测试用例2: 无效ObjectId格式
echo ""
echo "3. 测试无效ObjectId格式..."
INVALID_RESPONSE=$(curl -s -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"targetId\": \"$INVALID_OBJECT_ID\",
    \"targetType\": \"guwen\",
    \"content\": \"测试无效ID\"
  }")

echo "响应: $INVALID_RESPONSE"

if echo "$INVALID_RESPONSE" | grep -q '"code":400'; then
    echo "✅ 无效ObjectId正确被拒绝"
else
    echo "❌ 无效ObjectId处理异常"
fi

# 测试用例3: 空ObjectId
echo ""
echo "4. 测试空ObjectId..."
EMPTY_RESPONSE=$(curl -s -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "targetId": "",
    "targetType": "guwen",
    "content": "测试空ID"
  }')

echo "响应: $EMPTY_RESPONSE"

if echo "$EMPTY_RESPONSE" | grep -q '"code":400'; then
    echo "✅ 空ObjectId正确被拒绝"
else
    echo "❌ 空ObjectId处理异常"
fi

echo ""
echo "=== 测试完成 ==="
echo ""
echo "如果所有测试都通过，说明ObjectId转换修复生效！"
echo "如果有测试失败，请检查："
echo "1. Spring Boot应用是否正常运行"
echo "2. MongoDB是否正常运行"
echo "3. 测试用户是否存在"
echo "4. CommentServiceImpl.java修改是否正确"
