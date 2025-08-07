// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "JWT工具类最佳实践，安全性优先"
//   Quality_Check: "编译通过，JWT生成和验证功能完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    /**
     * JWT密钥
     * 必须至少64字节（512位）以满足HS512算法要求
     */
    @Value("${jwt.secret:poem-education-jwt-secure-secret-key-for-hs512-algorithm-minimum-512-bits-required-by-rfc7518-specification-2025}")
    private String jwtSecret;

    /**
     * JWT过期时间（秒）
     * 从配置文件读取，默认24小时
     */
    @Value("${jwt.expiration:86400}")
    private Long jwtExpirationInSeconds;
    
    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 从JWT令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 获取JWT令牌的过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 检查token基本格式
            if (token == null || token.trim().isEmpty()) {
                logger.debug("JWT令牌为空");
                return false;
            }

            // 检查JWT格式（应包含两个点号）
            if (token.split("\\.").length != 3) {
                logger.debug("JWT令牌格式错误: 应包含3个部分，实际包含{}个部分", token.split("\\.").length);
                return false;
            }

            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查JWT令牌是否过期
     * 
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    /**
     * 从JWT令牌中获取Claims
     * 
     * @param token JWT令牌
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 获取JWT过期时间（秒）
     *
     * @return 过期时间（秒）
     */
    public Long getExpirationInSeconds() {
        return jwtExpirationInSeconds;
    }
    
    /**
     * 刷新JWT令牌
     * 
     * @param token 原JWT令牌
     * @return 新JWT令牌
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            
            return generateToken(userId, username);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT令牌刷新失败: {}", e.getMessage());
            throw new RuntimeException("无效的JWT令牌");
        }
    }
}
// {{END_MODIFICATIONS}}
