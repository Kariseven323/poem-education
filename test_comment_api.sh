#!/bin/bash

# 评论API测试脚本 - 诊断400错误
# 用于验证评论提交问题的根本原因

echo "=== 评论API 400错误诊断 ==="

# 测试配置
API_BASE="http://localhost:8080/api/v1"
VALID_OBJECT_ID="5b9a0254367d5caccce1aa13"

echo "注意：请确保Spring Boot应用正在运行并查看控制台日志"
echo "应用日志将显示详细的错误信息"
echo ""

echo "1. 测试评论API无认证访问（应该成功，因为配置了permitAll）..."

# 测试用例1: 无认证的评论提交
echo ""
echo "测试无认证评论提交..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d "{
    \"targetId\": \"$VALID_OBJECT_ID\",
    \"targetType\": \"guwen\",
    \"content\": \"测试评论 - $(date)\"
  }")

echo "响应内容:"
echo "$RESPONSE"

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "HTTP状态码: $HTTP_CODE"

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ 无认证评论提交成功"
elif [ "$HTTP_CODE" = "400" ]; then
    echo "❌ 400错误 - 可能是ObjectId验证问题"
elif [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
    echo "❌ 认证问题 - 安全配置可能需要重启应用"
else
    echo "❌ 其他错误: $HTTP_CODE"
fi

# 测试用例2: 无效ObjectId格式
echo ""
echo "2. 测试无效ObjectId格式..."
INVALID_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "targetId": "invalid_id",
    "targetType": "guwen",
    "content": "测试无效ID"
  }')

echo "响应内容:"
echo "$INVALID_RESPONSE"

INVALID_HTTP_CODE=$(echo "$INVALID_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "HTTP状态码: $INVALID_HTTP_CODE"

if [ "$INVALID_HTTP_CODE" = "400" ]; then
    echo "✅ 无效ObjectId正确被拒绝"
else
    echo "❌ 无效ObjectId处理异常: $INVALID_HTTP_CODE"
fi

# 测试用例3: 空ObjectId
echo ""
echo "3. 测试空ObjectId..."
EMPTY_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "${API_BASE}/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "targetId": "",
    "targetType": "guwen",
    "content": "测试空ID"
  }')

echo "响应内容:"
echo "$EMPTY_RESPONSE"

EMPTY_HTTP_CODE=$(echo "$EMPTY_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "HTTP状态码: $EMPTY_HTTP_CODE"

if [ "$EMPTY_HTTP_CODE" = "400" ]; then
    echo "✅ 空ObjectId正确被拒绝"
else
    echo "❌ 空ObjectId处理异常: $EMPTY_HTTP_CODE"
fi

# 测试用例4: 检查GET评论接口
echo ""
echo "4. 测试GET评论接口..."
GET_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X GET "${API_BASE}/comments?targetId=$VALID_OBJECT_ID&targetType=guwen")

GET_HTTP_CODE=$(echo "$GET_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "GET评论HTTP状态码: $GET_HTTP_CODE"

if [ "$GET_HTTP_CODE" = "200" ]; then
    echo "✅ GET评论接口正常"
else
    echo "❌ GET评论接口异常: $GET_HTTP_CODE"
fi

echo ""
echo "=== 诊断总结 ==="
echo ""

if [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
    echo "🔍 问题诊断: 认证/授权问题"
    echo "原因: Spring Security配置需要重启应用才能生效"
    echo "解决方案: 重启Spring Boot应用"
elif [ "$HTTP_CODE" = "400" ]; then
    echo "🔍 问题诊断: 请求参数问题"
    echo "可能原因:"
    echo "1. ObjectId验证逻辑问题"
    echo "2. 请求体格式问题"
    echo "3. 必填字段缺失"
    echo "建议: 检查应用日志获取详细错误信息"
elif [ "$HTTP_CODE" = "200" ]; then
    echo "🎉 评论API工作正常！"
    echo "ObjectId转换和安全配置都已正确"
else
    echo "🔍 未知问题，HTTP状态码: $HTTP_CODE"
    echo "建议: 检查应用是否正常运行，查看应用日志"
fi

echo ""
echo "下一步建议:"
echo "1. 如果是认证问题，重启Spring Boot应用"
echo "2. 如果是400错误，检查应用日志中的详细错误信息"
echo "3. 验证前端发送的请求格式是否正确"
