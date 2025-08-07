// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class RegisterRequest {
    
    /**
     * 用户名
     * 必填，长度3-50字符，只能包含字母、数字、下划线
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    private String username;
    
    /**
     * 邮箱
     * 必填，格式验证
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    /**
     * 密码
     * 必填，长度6-20字符，至少包含字母和数字
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20字符之间")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$", 
             message = "密码必须包含至少一个字母和一个数字")
    private String password;
    
    /**
     * 昵称
     * 可选，长度1-50字符
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    // 默认构造函数
    public RegisterRequest() {
    }
    
    // 构造函数
    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Getter and Setter methods
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
