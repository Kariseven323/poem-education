// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "jwt-auth-filter-creation"
//   Timestamp: "2025-08-07T14:41:53+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Security JWT Filter最佳实践"
//   Quality_Check: "编译通过，JWT认证逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.security;

import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 用于处理JWT令牌验证和用户认证
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        Long userId = null;
        
        // 检查Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                // 验证token并提取用户信息
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.getUsernameFromToken(token);
                    userId = jwtUtil.getUserIdFromToken(token);
                    logger.debug("JWT验证成功: username={}, userId={}", username, userId);
                } else {
                    logger.debug("JWT验证失败: token无效");
                }
            } catch (Exception e) {
                logger.debug("JWT解析失败: {}", e.getMessage());
            }
        }
        
        // 如果token有效且当前没有认证信息，则设置认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 创建认证对象
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 设置到SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug("设置用户认证信息: userId={}", userId);
        }
        
        filterChain.doFilter(request, response);
    }
}
// {{END_MODIFICATIONS}}
