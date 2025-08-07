// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "5a312240-0eee-4528-b331-40ce70d611fb"
//   Timestamp: "2025-08-07T11:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "错误码管理最佳实践"
//   Quality_Check: "编译通过，错误码定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.constant;

/**
 * 错误码常量类
 * 定义系统中所有可能的错误码和对应的错误消息
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public final class ErrorCode {
    
    // ========== 通用错误码 ==========
    /** 成功 */
    public static final int SUCCESS = 200;
    public static final String SUCCESS_MSG = "success";
    
    /** 客户端错误 */
    public static final int BAD_REQUEST = 400;
    public static final String BAD_REQUEST_MSG = "请求参数错误";
    
    /** 未授权 */
    public static final int UNAUTHORIZED = 401;
    public static final String UNAUTHORIZED_MSG = "未授权访问";
    
    /** 禁止访问 */
    public static final int FORBIDDEN = 403;
    public static final String FORBIDDEN_MSG = "禁止访问";
    
    /** 资源不存在 */
    public static final int NOT_FOUND = 404;
    public static final String NOT_FOUND_MSG = "资源不存在";
    
    /** 方法不允许 */
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final String METHOD_NOT_ALLOWED_MSG = "请求方法不允许";
    
    /** 服务器内部错误 */
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final String INTERNAL_SERVER_ERROR_MSG = "服务器内部错误";
    
    // ========== 用户相关错误码 (1000-1999) ==========
    /** 用户不存在 */
    public static final int USER_NOT_FOUND = 1001;
    public static final String USER_NOT_FOUND_MSG = "用户不存在";
    
    /** 用户名已存在 */
    public static final int USERNAME_EXISTS = 1002;
    public static final String USERNAME_EXISTS_MSG = "用户名已存在";
    
    /** 邮箱已存在 */
    public static final int EMAIL_EXISTS = 1003;
    public static final String EMAIL_EXISTS_MSG = "邮箱已存在";
    
    /** 密码错误 */
    public static final int PASSWORD_ERROR = 1004;
    public static final String PASSWORD_ERROR_MSG = "密码错误";
    
    /** 用户已被禁用 */
    public static final int USER_DISABLED = 1005;
    public static final String USER_DISABLED_MSG = "用户已被禁用";
    
    /** JWT Token无效 */
    public static final int INVALID_TOKEN = 1006;
    public static final String INVALID_TOKEN_MSG = "Token无效";
    
    /** JWT Token已过期 */
    public static final int TOKEN_EXPIRED = 1007;
    public static final String TOKEN_EXPIRED_MSG = "Token已过期";
    
    // ========== 古文相关错误码 (2000-2999) ==========
    /** 古文不存在 */
    public static final int GUWEN_NOT_FOUND = 2001;
    public static final String GUWEN_NOT_FOUND_MSG = "古文不存在";
    
    /** 古文搜索参数错误 */
    public static final int GUWEN_SEARCH_PARAM_ERROR = 2002;
    public static final String GUWEN_SEARCH_PARAM_ERROR_MSG = "搜索参数错误";
    
    // ========== 评论相关错误码 (3000-3999) ==========
    /** 评论不存在 */
    public static final int COMMENT_NOT_FOUND = 3001;
    public static final String COMMENT_NOT_FOUND_MSG = "评论不存在";
    
    /** 评论内容为空 */
    public static final int COMMENT_CONTENT_EMPTY = 3002;
    public static final String COMMENT_CONTENT_EMPTY_MSG = "评论内容不能为空";
    
    /** 评论内容过长 */
    public static final int COMMENT_CONTENT_TOO_LONG = 3003;
    public static final String COMMENT_CONTENT_TOO_LONG_MSG = "评论内容过长";
    
    /** 评论层级过深 */
    public static final int COMMENT_LEVEL_TOO_DEEP = 3004;
    public static final String COMMENT_LEVEL_TOO_DEEP_MSG = "评论层级过深";
    
    /** 无权限操作评论 */
    public static final int COMMENT_NO_PERMISSION = 3005;
    public static final String COMMENT_NO_PERMISSION_MSG = "无权限操作此评论";
    
    // ========== 创作相关错误码 (4000-4999) ==========
    /** 创作不存在 */
    public static final int CREATION_NOT_FOUND = 4001;
    public static final String CREATION_NOT_FOUND_MSG = "创作不存在";
    
    /** 创作标题为空 */
    public static final int CREATION_TITLE_EMPTY = 4002;
    public static final String CREATION_TITLE_EMPTY_MSG = "创作标题不能为空";
    
    /** 创作内容为空 */
    public static final int CREATION_CONTENT_EMPTY = 4003;
    public static final String CREATION_CONTENT_EMPTY_MSG = "创作内容不能为空";
    
    /** 创作内容过长 */
    public static final int CREATION_CONTENT_TOO_LONG = 4004;
    public static final String CREATION_CONTENT_TOO_LONG_MSG = "创作内容过长";
    
    /** 无权限操作创作 */
    public static final int CREATION_NO_PERMISSION = 4005;
    public static final String CREATION_NO_PERMISSION_MSG = "无权限操作此创作";
    
    // ========== 用户行为相关错误码 (5000-5999) ==========
    /** 重复操作 */
    public static final int DUPLICATE_ACTION = 5001;
    public static final String DUPLICATE_ACTION_MSG = "重复操作";
    
    /** 操作目标不存在 */
    public static final int ACTION_TARGET_NOT_FOUND = 5002;
    public static final String ACTION_TARGET_NOT_FOUND_MSG = "操作目标不存在";
    
    /** 无效的操作类型 */
    public static final int INVALID_ACTION_TYPE = 5003;
    public static final String INVALID_ACTION_TYPE_MSG = "无效的操作类型";
    
    // ========== 数据库相关错误码 (6000-6999) ==========
    /** 数据库连接失败 */
    public static final int DATABASE_CONNECTION_ERROR = 6001;
    public static final String DATABASE_CONNECTION_ERROR_MSG = "数据库连接失败";
    
    /** 数据库操作失败 */
    public static final int DATABASE_OPERATION_ERROR = 6002;
    public static final String DATABASE_OPERATION_ERROR_MSG = "数据库操作失败";
    
    /** 数据完整性约束违反 */
    public static final int DATABASE_CONSTRAINT_VIOLATION = 6003;
    public static final String DATABASE_CONSTRAINT_VIOLATION_MSG = "数据完整性约束违反";
    
    // ========== 缓存相关错误码 (7000-7999) ==========
    /** 缓存操作失败 */
    public static final int CACHE_OPERATION_ERROR = 7001;
    public static final String CACHE_OPERATION_ERROR_MSG = "缓存操作失败";
    
    // ========== 文件相关错误码 (8000-8999) ==========
    /** 文件上传失败 */
    public static final int FILE_UPLOAD_ERROR = 8001;
    public static final String FILE_UPLOAD_ERROR_MSG = "文件上传失败";
    
    /** 文件类型不支持 */
    public static final int FILE_TYPE_NOT_SUPPORTED = 8002;
    public static final String FILE_TYPE_NOT_SUPPORTED_MSG = "文件类型不支持";
    
    /** 文件大小超限 */
    public static final int FILE_SIZE_EXCEEDED = 8003;
    public static final String FILE_SIZE_EXCEEDED_MSG = "文件大小超限";
    
    // ========== 参数验证相关错误码 (9000-9999) ==========
    /** 参数验证失败 */
    public static final int VALIDATION_ERROR = 9001;
    public static final String VALIDATION_ERROR_MSG = "参数验证失败";
    
    /** 必填参数缺失 */
    public static final int REQUIRED_PARAM_MISSING = 9002;
    public static final String REQUIRED_PARAM_MISSING_MSG = "必填参数缺失";
    
    /** 参数格式错误 */
    public static final int PARAM_FORMAT_ERROR = 9003;
    public static final String PARAM_FORMAT_ERROR_MSG = "参数格式错误";
    
    /**
     * 私有构造函数，防止实例化
     */
    private ErrorCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
// {{END_MODIFICATIONS}}
