// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.response.Result;
import com.poem.education.dto.request.UpdateProfileRequest;
import com.poem.education.dto.response.UserDTO;
import com.poem.education.service.UserService;
import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 * 处理用户信息管理相关接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取当前用户信息
     * GET /api/v1/users/profile
     * 
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/profile")
    public Result<UserDTO> getProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        logger.info("获取用户信息: userId={}", userId);
        
        UserDTO userDTO = userService.getUserById(userId);
        
        return Result.success(userDTO, "获取用户信息成功");
    }
    
    /**
     * 更新当前用户信息
     * PUT /api/v1/users/profile
     * 
     * @param request HTTP请求
     * @param updateRequest 更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public Result<UserDTO> updateProfile(HttpServletRequest request, 
                                       @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = getCurrentUserId(request);
        logger.info("更新用户信息: userId={}", userId);
        
        UserDTO userDTO = userService.updateProfile(userId, updateRequest);
        
        logger.info("用户信息更新成功: userId={}", userId);
        return Result.success(userDTO, "用户信息更新成功");
    }
    
    /**
     * 根据用户ID获取用户信息（公开接口）
     * GET /api/v1/users/{userId}
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserDTO> getUserById(@PathVariable Long userId) {
        logger.info("获取用户信息: userId={}", userId);
        
        UserDTO userDTO = userService.getUserById(userId);
        
        // 隐藏敏感信息
        userDTO.setEmail(null);
        
        return Result.success(userDTO, "获取用户信息成功");
    }
    
    /**
     * 根据用户名获取用户信息（公开接口）
     * GET /api/v1/users/by-username/{username}
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/by-username/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        logger.info("根据用户名获取用户信息: username={}", username);
        
        UserDTO userDTO = userService.getUserByUsername(username);
        
        // 隐藏敏感信息
        userDTO.setEmail(null);
        
        return Result.success(userDTO, "获取用户信息成功");
    }
    
    /**
     * 从HTTP请求中获取当前用户ID
     * 
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未提供有效的认证令牌");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("无效的认证令牌");
        }
        
        return jwtUtil.getUserIdFromToken(token);
    }
}
// {{END_MODIFICATIONS}}
