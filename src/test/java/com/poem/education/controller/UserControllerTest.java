package com.poem.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poem.education.config.TestConfig;
import com.poem.education.dto.request.LoginRequest;
import com.poem.education.dto.request.RegisterRequest;
import com.poem.education.dto.request.UpdateProfileRequest;
import com.poem.education.dto.response.LoginResponse;
import com.poem.education.dto.response.UserDTO;
import com.poem.education.service.UserService;
import com.poem.education.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController集成测试
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@WebMvcTest(UserController.class)
@Import(TestConfig.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private UserDTO testUserDTO;
    private LoginResponse testLoginResponse;
    private String testToken;
    
    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setNickname("Test User");
        testUserDTO.setStatus(1);
        testUserDTO.setCreatedAt(LocalDateTime.now());
        testUserDTO.setUpdatedAt(LocalDateTime.now());
        
        testLoginResponse = new LoginResponse();
        testLoginResponse.setAccessToken("jwt-token");
        testLoginResponse.setTokenType("Bearer");
        testLoginResponse.setExpiresIn(3600L);
        testLoginResponse.setUser(testUserDTO);
        
        testToken = "Bearer jwt-token";
    }
    
    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNickname("Test User");
        
        when(userService.register(any(RegisterRequest.class))).thenReturn(testUserDTO);
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
        
        verify(userService).register(any(RegisterRequest.class));
    }
    
    @Test
    void testRegister_ValidationError() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // 空用户名
        request.setEmail("invalid-email"); // 无效邮箱
        request.setPassword("123"); // 密码太短
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(userService, never()).register(any(RegisterRequest.class));
    }
    
    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        
        when(userService.login(any(LoginRequest.class))).thenReturn(testLoginResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
        
        verify(userService).login(any(LoginRequest.class));
    }
    
    @Test
    void testLogin_ValidationError() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername(""); // 空用户名
        request.setPassword(""); // 空密码
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(userService, never()).login(any(LoginRequest.class));
    }
    
    @Test
    void testCheckUsername_Available() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/check-username")
                .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByUsername("newuser");
    }
    
    @Test
    void testCheckUsername_NotAvailable() throws Exception {
        // Given
        when(userService.isUsernameAvailable("existinguser")).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/check-username")
                .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
        
        verify(userService).isUsernameAvailable("existinguser");
    }
    
    @Test
    void testCheckEmail_Available() throws Exception {
        // Given
        when(userService.isEmailAvailable("new@example.com")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(userService).isEmailAvailable("new@example.com");
    }
    
    @Test
    void testGetProfile_Success() throws Exception {
        // Given
        when(jwtUtil.getUserIdFromToken("jwt-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(testUserDTO);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/profile")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
        
        verify(jwtUtil).getUserIdFromToken("jwt-token");
        verify(userService).getUserById(1L);
    }
    
    @Test
    void testGetProfile_NoToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isInternalServerError());
        
        verify(jwtUtil, never()).getUserIdFromToken(anyString());
        verify(userService, never()).getUserById(anyLong());
    }
    
    @Test
    void testUpdateProfile_Success() throws Exception {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("Updated Nickname");
        request.setAvatar("new-avatar.jpg");
        request.setBio("Updated bio");
        
        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setNickname("Updated Nickname");
        
        when(jwtUtil.getUserIdFromToken("jwt-token")).thenReturn(1L);
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(updatedUser);
        
        // When & Then
        mockMvc.perform(put("/api/v1/users/profile")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新个人信息成功"))
                .andExpect(jsonPath("$.data.nickname").value("Updated Nickname"));
        
        verify(jwtUtil).getUserIdFromToken("jwt-token");
        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }
    
    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUserDTO);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
        
        verify(userService).getUserById(1L);
    }
    
    @Test
    void testGetUserByUsername_Success() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/by-username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
        
        verify(userService).getUserByUsername("testuser");
    }
}
