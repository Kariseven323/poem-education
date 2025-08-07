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
import com.poem.education.dto.request.LoginRequest;
import com.poem.education.dto.request.RegisterRequest;
import com.poem.education.dto.response.LoginResponse;
import com.poem.education.dto.response.UserDTO;
import com.poem.education.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 处理用户注册、登录等认证相关接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * POST /api/v1/auth/register
     * 
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("用户注册请求: username={}", request.getUsername());
        
        UserDTO userDTO = userService.register(request);
        
        logger.info("用户注册成功: userId={}", userDTO.getId());
        return Result.success(userDTO, "注册成功");
    }
    
    /**
     * 用户登录
     * POST /api/v1/auth/login
     * 
     * @param request 登录请求
     * @return 登录结果（包含JWT令牌）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("用户登录请求: username={}", request.getUsername());
        
        LoginResponse loginResponse = userService.login(request);
        
        logger.info("用户登录成功: username={}", request.getUsername());
        return Result.success(loginResponse, "登录成功");
    }
    
    /**
     * 检查用户名是否可用
     * GET /api/v1/auth/check-username?username=xxx
     * 
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(!exists, exists ? "用户名已存在" : "用户名可用");
    }
    
    /**
     * 检查邮箱是否可用
     * GET /api/v1/auth/check-email?email=xxx
     * 
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(!exists, exists ? "邮箱已存在" : "邮箱可用");
    }
}
// {{END_MODIFICATIONS}}
