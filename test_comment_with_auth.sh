#!/bin/bash

# 带认证的评论API测试脚本
echo "=== 带认证的评论API测试 ==="

API_BASE="http://localhost:8080/api/v1"
VALID_OBJECT_ID="5b9a0254367d5caccce1aa13"

echo "1. 注册测试用户..."
REGISTER_RESPONSE=$(curl -s -X POST "${API_BASE}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_comment",
    "email": "testuser_comment@example.com",
    "password": "password123",
    "nickname": "测试用户"
  }')

echo "注册响应: $REGISTER_RESPONSE"

echo ""
echo "2. 用户登录获取token..."
LOGIN_RESPONSE=$(curl -s -X POST "${API_BASE}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_comment",
    "password": "password123"
  }')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "提取的Token: $TOKEN"

if [ -z "$TOKEN" ]; then
    echo "❌ 无法获取token，尝试使用已存在的用户登录..."
    
    # 尝试使用可能已存在的用户
    LOGIN_RESPONSE=$(curl -s -X POST "${API_BASE}/auth/login" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "admin",
        "password": "admin123"
      }')
    
    echo "管理员登录响应: $LOGIN_RESPONSE"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "管理员Token: $TOKEN"
fi

if [ -n "$TOKEN" ]; then
    echo ""
    echo "3. 使用token创建评论..."
    COMMENT_RESPONSE=$(curl -s -X POST "${API_BASE}/comments" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"targetId\": \"$VALID_OBJECT_ID\",
        \"targetType\": \"guwen\",
        \"content\": \"这是一条测试评论 - $(date)\"
      }")
    
    echo "评论创建响应: $COMMENT_RESPONSE"
    
    echo ""
    echo "4. 检查评论是否创建成功..."
    GET_COMMENTS_RESPONSE=$(curl -s "${API_BASE}/comments?targetId=$VALID_OBJECT_ID&targetType=guwen")
    echo "获取评论响应: $GET_COMMENTS_RESPONSE"
    
    # 检查评论数量
    COMMENT_COUNT=$(echo "$GET_COMMENTS_RESPONSE" | grep -o '"total":[0-9]*' | cut -d':' -f2)
    echo "评论总数: $COMMENT_COUNT"
    
    if [ "$COMMENT_COUNT" -gt 0 ]; then
        echo "✅ 评论创建成功！现在前端应该能显示评论了。"
    else
        echo "❌ 评论创建可能失败，请检查响应。"
    fi
else
    echo "❌ 无法获取有效的认证token"
fi

echo ""
echo "测试完成！"
