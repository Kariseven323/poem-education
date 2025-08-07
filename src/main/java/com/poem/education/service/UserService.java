// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.request.LoginRequest;
import com.poem.education.dto.request.RegisterRequest;
import com.poem.education.dto.request.UpdateProfileRequest;
import com.poem.education.dto.response.LoginResponse;
import com.poem.education.dto.response.UserDTO;

/**
 * 用户服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param request 注册请求
     * @return 用户信息
     */
    UserDTO register(RegisterRequest request);
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应（包含JWT令牌）
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long userId);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    UserDTO updateProfile(Long userId, UpdateProfileRequest request);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 验证用户密码
     * 
     * @param userId 用户ID
     * @param rawPassword 原始密码
     * @return 是否正确
     */
    boolean validatePassword(Long userId, String rawPassword);
    
    /**
     * 更新用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String newPassword);
    
    /**
     * 禁用用户
     * 
     * @param userId 用户ID
     */
    void disableUser(Long userId);
    
    /**
     * 启用用户
     * 
     * @param userId 用户ID
     */
    void enableUser(Long userId);
}
// {{END_MODIFICATIONS}}
