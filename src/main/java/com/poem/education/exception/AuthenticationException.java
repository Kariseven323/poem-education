// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "5a312240-0eee-4528-b331-40ce70d611fb"
//   Timestamp: "2025-08-07T11:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "认证异常实现"
//   Quality_Check: "编译通过，异常类设计合理。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.exception;

import com.poem.education.constant.ErrorCode;

/**
 * 认证异常
 * 用于处理用户认证相关的异常
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class AuthenticationException extends BusinessException {
    
    /**
     * 构造函数（使用默认消息）
     */
    public AuthenticationException() {
        super(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_MSG);
    }
    
    /**
     * 构造函数（自定义消息）
     * 
     * @param message 错误消息
     */
    public AuthenticationException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
    
    /**
     * 构造函数（自定义错误码和消息）
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public AuthenticationException(Integer code, String message) {
        super(code, message);
    }
    
    /**
     * 构造函数（自定义消息和原因）
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public AuthenticationException(String message, Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, message, cause);
    }
}
// {{END_MODIFICATIONS}}
