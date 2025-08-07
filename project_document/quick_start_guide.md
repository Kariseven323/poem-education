# 诗词交流鉴赏平台 - 快速开始指南

## 🚀 5分钟快速启动

### 前置条件
- Java 11+
- Maven 3.6+
- MySQL 8.0+
- MongoDB 5.0+
- Redis 6.0+

### 第一步：数据库准备

#### 1. MySQL数据库初始化
```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source database/mysql_init.sql

# 验证表创建
USE poem_education;
SHOW TABLES;
```

#### 2. MongoDB数据库扩展
```bash
# 连接MongoDB
mongo

# 执行扩展脚本
load("database/mongodb_extend.js")

# 验证集合
use poem_education
show collections
```

#### 3. Redis启动
```bash
# 启动Redis服务
redis-server

# 验证连接
redis-cli ping
```

### 第二步：应用配置

#### 创建application-dev.yml
```yaml
server:
  port: 8080

spring:
  # MySQL配置
  datasource:
    url: jdbc:mysql://localhost:3306/poem_education?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: poem_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  # MongoDB配置
  data:
    mongodb:
      host: localhost
      port: 27017
      database: poem_education
      
  # Redis配置
  redis:
    host: localhost
    port: 6379
    database: 0

# JWT配置
jwt:
  secret: your-secret-key
  expiration: 86400

# 日志配置
logging:
  level:
    com.poem.education: debug
```

### 第三步：项目结构创建

#### 基础包结构
```
src/main/java/com/poem/education/
├── PoemEducationApplication.java
├── config/
│   ├── DatabaseConfig.java
│   ├── RedisConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── GuwenController.java
│   ├── UserController.java
│   └── CommentController.java
├── service/
│   ├── AuthService.java
│   ├── GuwenService.java
│   ├── UserService.java
│   └── CommentService.java
├── repository/
│   ├── mysql/
│   │   ├── UserRepository.java
│   │   └── UserActionRepository.java
│   └── mongodb/
│       ├── GuwenRepository.java
│       └── CommentRepository.java
├── entity/
│   ├── mysql/
│   │   ├── User.java
│   │   └── UserAction.java
│   └── mongodb/
│       ├── Guwen.java
│       └── Comment.java
├── dto/
│   ├── request/
│   └── response/
└── utils/
```

### 第四步：核心代码示例

#### 主启动类
```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.poem.education.repository.mysql")
@EnableMongoRepositories(basePackages = "com.poem.education.repository.mongodb")
public class PoemEducationApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoemEducationApplication.class, args);
    }
}
```

#### 数据库配置
```java
@Configuration
public class DatabaseConfig {
    
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create(), "poem_education");
    }
}
```

#### 古文实体类
```java
@Document(collection = "guwen")
@Data
public class Guwen {
    @Id
    private ObjectId id;
    private String title;
    private String dynasty;
    private String writer;
    private String content;
    private List<String> type;
    private String remark;
    private String shangxi;
    private String translation;
    private String audioUrl;
}
```

#### 古文控制器
```java
@RestController
@RequestMapping("/api/v1/guwen")
public class GuwenController {
    
    @Autowired
    private GuwenService guwenService;
    
    @GetMapping
    public Result<PageResult<GuwenDTO>> getGuwenList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String writer) {
        
        PageResult<GuwenDTO> result = guwenService.getGuwenList(page, size, dynasty, writer);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<GuwenDetailDTO> getGuwenDetail(@PathVariable String id) {
        GuwenDetailDTO detail = guwenService.getGuwenDetail(id);
        return Result.success(detail);
    }
}
```

### 第五步：测试验证

#### 启动应用
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

#### API测试
```bash
# 测试古文列表接口
curl "http://localhost:8080/api/v1/guwen?page=1&size=5"

# 测试古文详情接口
curl "http://localhost:8080/api/v1/guwen/{objectId}"

# 测试搜索接口
curl -X POST "http://localhost:8080/api/v1/guwen/search" \
  -H "Content-Type: application/json" \
  -d '{"keyword":"明月","dynasty":"唐"}'
```

## 📋 开发检查清单

### 环境检查
- [ ] Java 11+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] MySQL 8.0+ 已安装并运行
- [ ] MongoDB 5.0+ 已安装并运行
- [ ] Redis 6.0+ 已安装并运行

### 数据库检查
- [ ] MySQL数据库 `poem_education` 已创建
- [ ] MySQL表结构已初始化
- [ ] MongoDB集合已扩展
- [ ] 数据库连接配置正确

### 应用检查
- [ ] 项目结构已创建
- [ ] 配置文件已设置
- [ ] 依赖已添加到pom.xml
- [ ] 应用可以正常启动

### 功能检查
- [ ] 古文列表接口正常
- [ ] 古文详情接口正常
- [ ] 搜索功能正常
- [ ] 用户注册登录正常
- [ ] 评论功能正常

## 🔧 常见问题解决

### 数据库连接问题
```yaml
问题: 连接MySQL失败
解决: 检查用户权限和密码配置

问题: 连接MongoDB失败
解决: 确认MongoDB服务已启动，端口正确

问题: Redis连接失败
解决: 检查Redis服务状态和配置
```

### 应用启动问题
```yaml
问题: 端口被占用
解决: 修改server.port配置或停止占用进程

问题: 依赖冲突
解决: 检查Maven依赖版本兼容性

问题: 配置文件错误
解决: 验证YAML格式和配置项
```

## 📚 下一步学习

1. **深入学习**: 阅读完整的[架构设计文档](./architecture.md)
2. **API开发**: 参考[API接口文档](./api_documentation.md)
3. **数据库优化**: 学习[数据库设计文档](./database_design.md)
4. **部署上线**: 查看[部署运维文档](./deployment_guide.md)
5. **代码规范**: 遵循[开发规范文档](./development_standards.md)

## 🎯 开发建议

1. **先熟悉现有数据**: 浏览MongoDB中的古文、句子、作者数据
2. **理解数据关联**: 掌握跨数据库的关联查询方式
3. **注重性能**: 合理使用缓存和索引优化
4. **保证数据一致性**: 实现可靠的数据同步机制
5. **完善测试**: 编写单元测试和集成测试

---

🎉 **恭喜！** 您已经完成了诗词交流鉴赏平台的快速启动。现在可以开始开发具体的业务功能了！
