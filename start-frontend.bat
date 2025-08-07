@echo off
echo ========================================
echo 诗词交流鉴赏平台 - 前端启动脚本
echo ========================================
echo.

cd /d "%~dp0"

echo 检查前端目录...
if not exist "frontend" (
    echo 错误: frontend 目录不存在！
    pause
    exit /b 1
)

cd frontend

echo 检查 package.json...
if not exist "package.json" (
    echo 错误: package.json 不存在！
    pause
    exit /b 1
)

echo 检查 node_modules...
if not exist "node_modules" (
    echo node_modules 不存在，正在安装依赖...
    echo.
    npm install
    if errorlevel 1 (
        echo 错误: 依赖安装失败！
        pause
        exit /b 1
    )
    echo 依赖安装完成！
    echo.
) else (
    echo node_modules 已存在，跳过依赖安装
    echo.
)

echo 启动前端开发服务器...
echo.
echo 前端地址: http://localhost:3000
echo 后端代理: http://localhost:8080
echo.
echo 请确保后端服务已启动在 8080 端口！
echo.
echo 按 Ctrl+C 停止服务器
echo ========================================
echo.

npm start

pause
