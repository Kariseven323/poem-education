package com.poem.education.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil测试类
 * 验证JWT密钥安全性和功能正确性
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        // 设置测试用的安全密钥（符合HS512要求的512位密钥）
        String secureSecret = "poem-education-jwt-secure-secret-key-for-hs512-algorithm-minimum-512-bits-required-by-rfc7518-specification-2025-secure-token-generation";
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secureSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationInSeconds", 86400L); // 24小时
    }
    
    @Test
    void testSecretKeyLength() {
        // 验证密钥长度符合HS512要求（至少512位）
        String secret = "poem-education-jwt-secure-secret-key-for-hs512-algorithm-minimum-512-bits-required-by-rfc7518-specification-2025-secure-token-generation";
        int bitLength = secret.getBytes().length * 8;
        
        assertTrue(bitLength >= 512, 
            String.format("JWT密钥长度为%d位，不满足HS512算法要求的最小512位", bitLength));
        
        System.out.println("JWT密钥长度: " + bitLength + " 位 (符合HS512要求)");
    }
    
    @Test
    void testGenerateTokenSuccess() {
        // 测试JWT令牌生成不会抛出WeakKeyException
        Long userId = 1L;
        String username = "testuser";
        
        assertDoesNotThrow(() -> {
            String token = jwtUtil.generateToken(userId, username);
            assertNotNull(token);
            assertFalse(token.isEmpty());
            System.out.println("生成的JWT令牌: " + token.substring(0, 50) + "...");
        }, "JWT令牌生成不应抛出WeakKeyException");
    }
    
    @Test
    void testTokenValidation() {
        // 测试JWT令牌验证功能
        Long userId = 1L;
        String username = "testuser";
        
        String token = jwtUtil.generateToken(userId, username);
        
        // 验证令牌有效性
        assertTrue(jwtUtil.validateToken(token), "生成的JWT令牌应该是有效的");
        
        // 验证令牌未过期
        assertFalse(jwtUtil.isTokenExpired(token), "新生成的JWT令牌不应该过期");
    }
    
    @Test
    void testExtractUserInfo() {
        // 测试从JWT令牌中提取用户信息
        Long userId = 123L;
        String username = "testuser";
        
        String token = jwtUtil.generateToken(userId, username);
        
        // 提取用户ID
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);
        assertEquals(userId, extractedUserId, "提取的用户ID应该与原始用户ID相同");
        
        // 提取用户名
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        assertEquals(username, extractedUsername, "提取的用户名应该与原始用户名相同");
        
        System.out.println("提取的用户ID: " + extractedUserId);
        System.out.println("提取的用户名: " + extractedUsername);
    }
    
    @Test
    void testTokenExpiration() {
        // 测试JWT令牌过期时间设置
        Long userId = 1L;
        String username = "testuser";
        
        String token = jwtUtil.generateToken(userId, username);
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
        
        assertNotNull(expirationDate, "JWT令牌应该有过期时间");
        
        Date now = new Date();
        assertTrue(expirationDate.after(now), "JWT令牌过期时间应该在当前时间之后");
        
        // 验证过期时间大约是24小时后（允许1分钟误差）
        long expectedExpiration = now.getTime() + 86400000L; // 24小时
        long actualExpiration = expirationDate.getTime();
        long difference = Math.abs(actualExpiration - expectedExpiration);
        
        assertTrue(difference < 60000L, "JWT令牌过期时间应该大约是24小时后");
        
        System.out.println("JWT令牌过期时间: " + expirationDate);
    }
    
    @Test
    void testGetExpirationInSeconds() {
        // 测试获取过期时间（秒）
        Long expirationInSeconds = jwtUtil.getExpirationInSeconds();
        assertEquals(86400L, expirationInSeconds, "JWT过期时间应该是86400秒（24小时）");
    }
    
    @Test
    void testRefreshToken() {
        // 测试JWT令牌刷新功能
        Long userId = 1L;
        String username = "testuser";
        
        String originalToken = jwtUtil.generateToken(userId, username);
        
        // 刷新令牌
        String refreshedToken = jwtUtil.refreshToken(originalToken);
        
        assertNotNull(refreshedToken, "刷新后的令牌不应该为空");
        assertNotEquals(originalToken, refreshedToken, "刷新后的令牌应该与原令牌不同");
        
        // 验证刷新后的令牌包含相同的用户信息
        assertEquals(userId, jwtUtil.getUserIdFromToken(refreshedToken));
        assertEquals(username, jwtUtil.getUsernameFromToken(refreshedToken));
        
        System.out.println("原始令牌: " + originalToken.substring(0, 50) + "...");
        System.out.println("刷新令牌: " + refreshedToken.substring(0, 50) + "...");
    }
    
    @Test
    void testInvalidTokenValidation() {
        // 测试无效令牌验证
        String invalidToken = "invalid.jwt.token";
        
        assertFalse(jwtUtil.validateToken(invalidToken), "无效的JWT令牌应该验证失败");
        assertTrue(jwtUtil.isTokenExpired(invalidToken), "无效的JWT令牌应该被认为已过期");
        
        // 测试刷新无效令牌应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            jwtUtil.refreshToken(invalidToken);
        }, "刷新无效令牌应该抛出异常");
    }
    
    @Test
    void testWeakKeyPrevention() {
        // 测试弱密钥预防 - 使用短密钥应该抛出WeakKeyException
        JwtUtil weakJwtUtil = new JwtUtil();
        String weakSecret = "short-key"; // 只有72位，远少于512位
        ReflectionTestUtils.setField(weakJwtUtil, "jwtSecret", weakSecret);
        ReflectionTestUtils.setField(weakJwtUtil, "jwtExpirationInSeconds", 86400L);
        
        assertThrows(WeakKeyException.class, () -> {
            weakJwtUtil.generateToken(1L, "testuser");
        }, "使用弱密钥应该抛出WeakKeyException");
        
        System.out.println("弱密钥测试通过：短密钥正确抛出WeakKeyException");
    }
}
