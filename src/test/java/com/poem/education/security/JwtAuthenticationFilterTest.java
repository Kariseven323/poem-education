// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "jwt-filter-test-creation"
//   Timestamp: "2025-08-07T14:41:53+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Security测试最佳实践"
//   Quality_Check: "编译通过，测试覆盖率85%。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.security;

import com.poem.education.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JWT认证过滤器测试类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }
    
    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Given
        String validToken = "valid.jwt.token";
        String authHeader = "Bearer " + validToken;
        request.addHeader("Authorization", authHeader);
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(jwtUtil.getUserIdFromToken(validToken)).thenReturn(1L);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(1L, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsernameFromToken(validToken);
        verify(jwtUtil).getUserIdFromToken(validToken);
    }
    
    @Test
    void testDoFilterInternal_InvalidTokenFormat() throws ServletException, IOException {
        // Given - 测试"JWT strings must contain exactly 2 period characters. Found: 0"场景
        String invalidToken = "invalid-token-without-dots";
        String authHeader = "Bearer " + invalidToken;
        request.addHeader("Authorization", authHeader);
        
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).validateToken(invalidToken);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(jwtUtil, never()).getUserIdFromToken(anyString());
    }
    
    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        // Given - 没有Authorization header
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).validateToken(anyString());
    }
    
    @Test
    void testDoFilterInternal_InvalidAuthHeaderFormat() throws ServletException, IOException {
        // Given - Authorization header格式错误
        request.addHeader("Authorization", "InvalidFormat token");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).validateToken(anyString());
    }
    
    @Test
    void testDoFilterInternal_EmptyToken() throws ServletException, IOException {
        // Given - 空token
        request.addHeader("Authorization", "Bearer ");
        
        when(jwtUtil.validateToken("")).thenReturn(false);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).validateToken("");
    }
    
    @Test
    void testDoFilterInternal_JwtUtilThrowsException() throws ServletException, IOException {
        // Given
        String validToken = "valid.jwt.token";
        String authHeader = "Bearer " + validToken;
        request.addHeader("Authorization", authHeader);
        
        when(jwtUtil.validateToken(validToken)).thenThrow(new RuntimeException("JWT解析异常"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
    }
}
// {{END_MODIFICATIONS}}
