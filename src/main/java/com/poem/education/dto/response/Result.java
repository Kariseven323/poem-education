// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "5a312240-0eee-4528-b331-40ce70d611fb"
//   Timestamp: "2025-08-07T11:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "统一响应格式最佳实践"
//   Quality_Check: "编译通过，响应格式符合API文档规范。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 统一响应格式类
 * 严格按照API文档定义的响应格式实现
 * 
 * @author poem-education-team
 * @since 2025-08-07
 * @param <T> 响应数据类型
 */
public class Result<T> {
    
    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误
     * 401: 未授权
     * 403: 禁止访问
     * 404: 资源不存在
     * 500: 服务器内部错误
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }
    
    /**
     * 错误响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.data = null;
        return result;
    }
    
    /**
     * 错误响应（默认500错误）
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    /**
     * 客户端错误响应（400）
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    /**
     * 未授权响应（401）
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }
    
    /**
     * 禁止访问响应（403）
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }
    
    /**
     * 资源不存在响应（404）
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
    
    // Getter and Setter methods
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
// {{END_MODIFICATIONS}}
