package com.poem.education.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poem.education.dto.response.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GlobalExceptionHandler测试
 * 验证异常处理器返回的Result对象能够正常序列化
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void testBusinessExceptionHandlerSerialization() throws Exception {
        // 模拟业务异常
        BusinessException businessException = new BusinessException(400, "用户名已存在");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/register");
        
        // 调用异常处理器
        Result<Void> result = exceptionHandler.handleBusinessException(businessException, request);
        
        // 验证Result对象
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("用户名已存在", result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
        
        // 验证JSON序列化
        String json = objectMapper.writeValueAsString(result);
        assertNotNull(json);
        
        // 验证JSON包含必要字段
        assertTrue(json.contains("\"code\":400"));
        assertTrue(json.contains("\"message\":\"用户名已存在\""));
        assertTrue(json.contains("\"data\":null"));
        assertTrue(json.contains("\"timestamp\":"));
        
        // 验证timestamp格式正确（yyyy-MM-dd HH:mm:ss）
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*"));
        
        System.out.println("Business exception result JSON: " + json);
        
        // 验证反序列化也能正常工作
        Result<Void> deserializedResult = objectMapper.readValue(json, Result.class);
        assertNotNull(deserializedResult);
        assertEquals(400, deserializedResult.getCode());
        assertEquals("用户名已存在", deserializedResult.getMessage());
    }
    
    @Test
    void testValidationExceptionHandlerSerialization() throws Exception {
        // 这里我们直接测试Result.error方法的序列化
        Result<Void> result = Result.error(400, "参数验证失败");
        
        // 验证JSON序列化
        String json = objectMapper.writeValueAsString(result);
        assertNotNull(json);
        
        // 验证JSON格式
        assertTrue(json.contains("\"code\":400"));
        assertTrue(json.contains("\"message\":\"参数验证失败\""));
        assertTrue(json.contains("\"timestamp\":"));
        
        // 验证timestamp格式
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*"));
        
        System.out.println("Validation exception result JSON: " + json);
    }
    
    @Test
    void testGenericExceptionHandlerSerialization() throws Exception {
        // 模拟通用异常
        Exception exception = new RuntimeException("Internal server error");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        
        // 调用异常处理器
        Result<Void> result = exceptionHandler.handleGenericException(exception, request);
        
        // 验证Result对象
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertNotNull(result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
        
        // 验证JSON序列化
        String json = objectMapper.writeValueAsString(result);
        assertNotNull(json);
        
        // 验证JSON包含必要字段
        assertTrue(json.contains("\"code\":500"));
        assertTrue(json.contains("\"timestamp\":"));
        
        // 验证timestamp格式正确
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*"));
        
        System.out.println("Generic exception result JSON: " + json);
    }
}
