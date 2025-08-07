#!/bin/bash

echo "========================================"
echo "诗词交流鉴赏平台 - 前端启动脚本"
echo "========================================"
echo

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "检查前端目录..."
if [ ! -d "frontend" ]; then
    echo "错误: frontend 目录不存在！"
    exit 1
fi

cd frontend

echo "检查 package.json..."
if [ ! -f "package.json" ]; then
    echo "错误: package.json 不存在！"
    exit 1
fi

echo "检查 node_modules..."
if [ ! -d "node_modules" ]; then
    echo "node_modules 不存在，正在安装依赖..."
    echo
    npm install
    if [ $? -ne 0 ]; then
        echo "错误: 依赖安装失败！"
        exit 1
    fi
    echo "依赖安装完成！"
    echo
else
    echo "node_modules 已存在，跳过依赖安装"
    echo
fi

echo "启动前端开发服务器..."
echo
echo "前端地址: http://localhost:3000"
echo "后端代理: http://localhost:8080"
echo
echo "请确保后端服务已启动在 8080 端口！"
echo
echo "按 Ctrl+C 停止服务器"
echo "========================================"
echo

npm start
