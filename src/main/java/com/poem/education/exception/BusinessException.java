// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "5a312240-0eee-4528-b331-40ce70d611fb"
//   Timestamp: "2025-08-07T11:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "异常处理最佳实践"
//   Quality_Check: "编译通过，异常类设计合理。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.exception;

/**
 * 业务异常基类
 * 所有业务相关的异常都应该继承此类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
