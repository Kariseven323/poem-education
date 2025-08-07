# 诗词交流鉴赏平台 - 开发规范文档

## 1. 代码规范

### 1.1 Java编码规范
遵循阿里巴巴Java开发手册规范

#### 命名规范
```java
// 类名：大驼峰命名
public class UserService {}

// 方法名：小驼峰命名
public void getUserInfo() {}

// 常量：全大写，下划线分隔
public static final String DEFAULT_ENCODING = "UTF-8";

// 包名：全小写，点分隔
package com.poem.education.service;
```

#### 注释规范
```java
/**
 * 用户服务类
 * 
 * @author 开发者姓名
 * @since 2025-08-07
 */
@Service
public class UserService {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     * @throws UserNotFoundException 用户不存在异常
     */
    public UserDTO getUserById(Long userId) {
        // 实现逻辑
    }
}
```

### 1.2 数据库规范

#### 表命名规范
- 表名：小写，下划线分隔，复数形式
- 字段名：小写，下划线分隔
- 索引名：idx_字段名
- 外键名：fk_表名_字段名

#### SQL编写规范
```sql
-- 关键字大写
SELECT u.id, u.username, u.email
FROM users u
WHERE u.status = 1
  AND u.created_at >= '2025-01-01'
ORDER BY u.created_at DESC
LIMIT 10;
```

## 2. 项目结构规范

### 2.1 包结构
```
com.poem.education/
├── controller/          # 控制器层
│   ├── UserController.java
│   └── PoemController.java
├── service/            # 业务逻辑层
│   ├── UserService.java
│   └── impl/
│       └── UserServiceImpl.java
├── repository/         # 数据访问层
│   ├── mysql/
│   │   └── UserRepository.java
│   └── mongodb/
│       └── PoemRepository.java
├── entity/             # 实体类
│   ├── mysql/
│   │   └── User.java
│   └── mongodb/
│       └── Poem.java
├── dto/                # 数据传输对象
│   ├── request/
│   └── response/
├── config/             # 配置类
├── security/           # 安全相关
├── utils/              # 工具类
├── exception/          # 异常处理
└── constant/           # 常量定义
```

### 2.2 分层架构规范

#### Controller层
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable @Min(1) Long id) {
        UserDTO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

#### Service层
```java
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("用户不存在"));
        return UserConverter.toDTO(user);
    }
}
```

## 3. API设计规范

### 3.1 RESTful API规范
- 使用HTTP动词表示操作：GET、POST、PUT、DELETE
- 使用名词表示资源：/users、/poems
- 使用HTTP状态码表示结果

### 3.2 统一响应格式
```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }
}
```

### 3.3 参数验证规范
```java
@Data
@Valid
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", 
             message = "密码必须包含大小写字母和数字，长度至少8位")
    private String password;
}
```

## 4. 异常处理规范

### 4.1 自定义异常
```java
public class BusinessException extends RuntimeException {
    private Integer code;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(404, message);
    }
}
```

### 4.2 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(400, message);
    }
}
```

## 5. 测试规范

### 5.1 单元测试
```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // When
        UserDTO result = userService.getUserById(userId);
        
        // Then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }
}
```

### 5.2 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetUser() {
        ResponseEntity<Result> response = restTemplate.getForEntity("/api/v1/users/1", Result.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## 6. Git提交规范

### 6.1 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 6.2 类型说明
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 6.3 示例
```
feat(user): 添加用户注册功能

- 实现用户注册API
- 添加邮箱验证
- 完善参数校验

Closes #123
```
