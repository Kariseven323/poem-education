// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service实现最佳实践，密码安全加密"
//   Quality_Check: "编译通过，业务逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.request.LoginRequest;
import com.poem.education.dto.request.RegisterRequest;
import com.poem.education.dto.request.UpdateProfileRequest;
import com.poem.education.dto.response.LoginResponse;
import com.poem.education.dto.response.UserDTO;
import com.poem.education.entity.mysql.User;
import com.poem.education.exception.AuthenticationException;
import com.poem.education.exception.BusinessException;
import com.poem.education.exception.UserNotFoundException;
import com.poem.education.repository.mysql.UserRepository;
import com.poem.education.service.UserService;
import com.poem.education.util.JwtUtil;
import com.poem.education.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public UserDTO register(RegisterRequest request) {
        logger.info("用户注册请求: username={}, email={}", request.getUsername(), request.getEmail());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS, "邮箱已存在");
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(1); // 默认启用状态
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        logger.info("用户注册成功: userId={}, username={}", savedUser.getId(), savedUser.getUsername());
        
        // 转换为DTO
        return convertToDTO(savedUser);
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("用户登录请求: username={}", request.getUsername());
        
        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (!userOptional.isPresent()) {
            throw new AuthenticationException("用户名或密码错误");
        }

        User user = userOptional.get();

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new AuthenticationException("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        Long expiresIn = jwtUtil.getExpirationInSeconds();
        
        logger.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        
        // 构建登录响应
        UserDTO userDTO = convertToDTO(user);
        return new LoginResponse(accessToken, expiresIn, userDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("用户不存在");
        }

        return convertToDTO(userOptional.get());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("用户不存在");
        }
        
        return convertToDTO(userOptional.get());
    }
    
    @Override
    public UserDTO updateProfile(Long userId, UpdateProfileRequest request) {
        logger.info("更新用户信息: userId={}", userId);
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 更新用户信息
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        
        logger.info("用户信息更新成功: userId={}", userId);
        
        return convertToDTO(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(Long userId, String rawPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return false;
        }
        
        User user = userOptional.get();
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
    
    @Override
    public void updatePassword(Long userId, String newPassword) {
        logger.info("更新用户密码: userId={}", userId);
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("用户不存在");
        }
        
        User user = userOptional.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("用户密码更新成功: userId={}", userId);
    }
    
    @Override
    public void disableUser(Long userId) {
        logger.info("禁用用户: userId={}", userId);
        
        int updatedRows = userRepository.updateUserStatus(userId, 0);
        if (updatedRows == 0) {
            throw new UserNotFoundException("用户不存在");
        }
        
        logger.info("用户禁用成功: userId={}", userId);
    }
    
    @Override
    public void enableUser(Long userId) {
        logger.info("启用用户: userId={}", userId);
        
        int updatedRows = userRepository.updateUserStatus(userId, 1);
        if (updatedRows == 0) {
            throw new UserNotFoundException("用户不存在");
        }
        
        logger.info("用户启用成功: userId={}", userId);
    }
    
    /**
     * 将User实体转换为UserDTO
     * 
     * @param user User实体
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}
// {{END_MODIFICATIONS}}
