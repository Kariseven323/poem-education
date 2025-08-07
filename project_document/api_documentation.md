# 诗词交流鉴赏平台 - API接口文档

## 1. API设计规范

### 1.1 基础规范
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **API版本**: v1
- **基础路径**: `/api/v1`

### 1.2 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2025-08-07T09:56:16+08:00"
}
```

### 1.3 状态码规范
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

## 2. 用户管理模块 API

### 2.1 用户注册
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "nickname": "string"
}
```

### 2.2 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

### 2.3 获取用户信息
```http
GET /api/v1/users/profile
Authorization: Bearer {token}
```

### 2.4 更新用户信息
```http
PUT /api/v1/users/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "string",
  "avatar": "string",
  "bio": "string"
}
```

## 3. 诗词管理模块 API（基于现有MongoDB数据）

### 3.1 获取古文列表
```http
GET /api/v1/guwen?page=1&size=20&dynasty=唐&writer=李白&type=诗
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "_id": "507f1f77bcf86cd799439011",
        "title": "静夜思",
        "dynasty": "唐",
        "writer": "李白",
        "content": "床前明月光，疑是地上霜。举头望明月，低头思故乡。",
        "type": ["诗"],
        "remark": "注释内容",
        "shangxi": "赏析内容",
        "translation": "翻译内容",
        "audioUrl": "音频链接",
        "writerInfo": {
          "headImageUrl": "头像链接",
          "simpleIntro": "简介"
        },
        "stats": {
          "viewCount": 1250,
          "likeCount": 89,
          "favoriteCount": 45,
          "commentCount": 12
        }
      }
    ],
    "total": 1000,
    "page": 1,
    "size": 20
  }
}
```

### 3.2 获取古文详情
```http
GET /api/v1/guwen/{objectId}
```

### 3.3 搜索古文
```http
POST /api/v1/guwen/search
Content-Type: application/json

{
  "keyword": "明月",
  "writer": "李白",
  "dynasty": "唐",
  "type": ["诗"],
  "page": 1,
  "size": 20
}
```

### 3.4 获取热门古文
```http
GET /api/v1/guwen/hot?period=daily&limit=10
```

### 3.5 获取作者信息
```http
GET /api/v1/writers/{objectId}
```

### 3.6 获取作者作品列表
```http
GET /api/v1/writers/{objectId}/works?page=1&size=20
```

### 3.7 获取句子列表
```http
GET /api/v1/sentences?page=1&size=20&keyword=明月&from=静夜思
```

### 3.8 随机获取句子
```http
GET /api/v1/sentences/random?count=5
```

## 4. 交流功能模块 API

### 4.1 获取评论列表（支持层级嵌套）
```http
GET /api/v1/comments?targetId={objectId}&targetType=guwen&page=1&size=20
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "_id": "507f1f77bcf86cd799439012",
        "targetId": "507f1f77bcf86cd799439011",
        "targetType": "guwen",
        "userId": 1,
        "content": "这首诗意境深远",
        "parentId": null,
        "level": 1,
        "path": "1",
        "likeCount": 5,
        "replyCount": 2,
        "status": 1,
        "createdAt": "2025-08-07T10:00:00Z",
        "userInfo": {
          "nickname": "诗词爱好者",
          "avatar": "头像链接"
        },
        "children": [
          {
            "_id": "507f1f77bcf86cd799439013",
            "content": "确实如此",
            "level": 2,
            "path": "1.1",
            "parentId": "507f1f77bcf86cd799439012"
          }
        ]
      }
    ]
  }
}
```

### 4.2 发表评论
```http
POST /api/v1/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "targetId": "507f1f77bcf86cd799439011",
  "targetType": "guwen",
  "content": "这首诗写得真好！",
  "parentId": "507f1f77bcf86cd799439012"
}
```

### 4.3 点赞/取消点赞
```http
POST /api/v1/actions/like
Authorization: Bearer {token}
Content-Type: application/json

{
  "targetId": "507f1f77bcf86cd799439011",
  "targetType": "guwen"
}
```

### 4.4 收藏/取消收藏
```http
POST /api/v1/actions/favorite
Authorization: Bearer {token}
Content-Type: application/json

{
  "targetId": "507f1f77bcf86cd799439011",
  "targetType": "guwen",
  "folderName": "我的收藏夹",
  "notes": "收藏备注"
}
```

### 4.5 获取用户收藏列表
```http
GET /api/v1/users/favorites?targetType=guwen&folderName=默认收藏夹&page=1&size=20
Authorization: Bearer {token}
```

### 4.6 分享内容
```http
POST /api/v1/actions/share
Authorization: Bearer {token}
Content-Type: application/json

{
  "targetId": "507f1f77bcf86cd799439011",
  "targetType": "guwen",
  "platform": "wechat"
}
```

## 5. 鉴赏功能模块 API

### 5.1 获取诗词解析
```http
GET /api/v1/poems/{poemId}/analysis
```

### 5.2 获取文学赏析
```http
GET /api/v1/poems/{poemId}/appreciation
```

## 6. 创作功能模块 API

### 6.1 提交创作作品
```http
POST /api/v1/creations
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "string",
  "content": "string",
  "style": "string",
  "description": "string"
}
```

### 6.2 获取AI评分
```http
GET /api/v1/creations/{creationId}/score
Authorization: Bearer {token}
```

### 6.3 生成雷达图
```http
GET /api/v1/creations/{creationId}/radar
Authorization: Bearer {token}
```
