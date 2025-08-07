# 诗词交流鉴赏平台 - 部署运维文档

## 1. 环境要求

### 1.1 基础环境
- **操作系统**: Linux (推荐 Ubuntu 20.04+)
- **Java**: OpenJDK 11+
- **Maven**: 3.6+
- **Docker**: 20.10+ (可选)

### 1.2 数据库环境
- **MySQL**: 8.0+
- **MongoDB**: 5.0+
- **Redis**: 6.0+

## 2. 本地开发环境搭建

### 2.1 数据库安装配置

#### MySQL配置
```bash
# 安装MySQL
sudo apt update
sudo apt install mysql-server

# 创建数据库
mysql -u root -p
CREATE DATABASE poem_education CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'poem_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON poem_education.* TO 'poem_user'@'localhost';
FLUSH PRIVILEGES;
```

#### MongoDB配置
```bash
# 安装MongoDB
wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list
sudo apt update
sudo apt install mongodb-org

# 启动MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod

# 创建数据库用户
mongo
use poem_education
db.createUser({
  user: "poem_user",
  pwd: "your_password",
  roles: ["readWrite"]
})
```

#### Redis配置
```bash
# 安装Redis
sudo apt install redis-server

# 配置Redis
sudo vim /etc/redis/redis.conf
# 设置密码: requirepass your_password

# 重启Redis
sudo systemctl restart redis-server
```

### 2.2 应用配置

#### application-dev.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/poem_education?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: poem_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  data:
    mongodb:
      host: localhost
      port: 27017
      database: poem_education
      username: poem_user
      password: your_password
      
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.poem.education: debug
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 3. 生产环境部署

### 3.1 Docker部署方案

#### Dockerfile
```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/poem-education-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - mongodb
      - redis
    networks:
      - poem-network

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: poem_education
      MYSQL_USER: poem_user
      MYSQL_PASSWORD: user_password
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - poem-network

  mongodb:
    image: mongo:5.0
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: root_password
      MONGO_INITDB_DATABASE: poem_education
    volumes:
      - mongodb_data:/data/db
    networks:
      - poem-network

  redis:
    image: redis:6.0-alpine
    command: redis-server --requirepass redis_password
    volumes:
      - redis_data:/data
    networks:
      - poem-network

volumes:
  mysql_data:
  mongodb_data:
  redis_data:

networks:
  poem-network:
    driver: bridge
```

### 3.2 部署脚本
```bash
#!/bin/bash
# deploy.sh

echo "开始部署诗词交流鉴赏平台..."

# 拉取最新代码
git pull origin main

# 构建应用
mvn clean package -DskipTests

# 构建Docker镜像
docker-compose build

# 启动服务
docker-compose up -d

echo "部署完成！"
echo "应用访问地址: http://localhost:8080"
```

## 4. 监控与维护

### 4.1 健康检查
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 4.2 日志管理
- 使用ELK Stack进行日志收集和分析
- 配置日志轮转策略
- 设置关键错误告警

### 4.3 备份策略
- MySQL: 每日全量备份 + 增量备份
- MongoDB: 定期导出重要集合
- Redis: RDB + AOF持久化
