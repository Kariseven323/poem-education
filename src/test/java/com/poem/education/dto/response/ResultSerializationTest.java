package com.poem.education.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result类JSON序列化测试
 * 验证timestamp字段的序列化是否正常工作
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
class ResultSerializationTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void testSuccessResultSerialization() throws Exception {
        // 创建成功响应
        Result<String> result = Result.success("test data");
        
        // 序列化为JSON
        String json = objectMapper.writeValueAsString(result);
        
        // 验证JSON包含必要字段
        assertNotNull(json);
        assertTrue(json.contains("\"code\":200"));
        assertTrue(json.contains("\"message\":\"success\""));
        assertTrue(json.contains("\"data\":\"test data\""));
        assertTrue(json.contains("\"timestamp\":"));
        
        // 验证timestamp格式正确（yyyy-MM-dd HH:mm:ss）
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*"));
        
        System.out.println("Success result JSON: " + json);
    }
    
    @Test
    void testErrorResultSerialization() throws Exception {
        // 创建错误响应
        Result<Void> result = Result.error(400, "用户名已存在");
        
        // 序列化为JSON
        String json = objectMapper.writeValueAsString(result);
        
        // 验证JSON包含必要字段
        assertNotNull(json);
        assertTrue(json.contains("\"code\":400"));
        assertTrue(json.contains("\"message\":\"用户名已存在\""));
        assertTrue(json.contains("\"data\":null"));
        assertTrue(json.contains("\"timestamp\":"));
        
        // 验证timestamp格式正确
        assertTrue(json.matches(".*\"timestamp\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*"));
        
        System.out.println("Error result JSON: " + json);
    }
    
    @Test
    void testResultDeserialization() throws Exception {
        // 创建测试JSON
        String json = "{\"code\":200,\"message\":\"success\",\"data\":\"test\",\"timestamp\":\"2025-08-07 14:30:00\"}";
        
        // 反序列化
        Result<String> result = objectMapper.readValue(json, Result.class);
        
        // 验证反序列化结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test", result.getData());
        assertNotNull(result.getTimestamp());
        
        System.out.println("Deserialized result: " + result);
    }
    
    @Test
    void testTimestampFieldSerialization() throws Exception {
        // 直接测试LocalDateTime序列化
        LocalDateTime now = LocalDateTime.of(2025, 8, 7, 14, 30, 0);
        Result<String> result = Result.success("test");
        result.setTimestamp(now);
        
        String json = objectMapper.writeValueAsString(result);
        
        // 验证时间格式
        assertTrue(json.contains("\"timestamp\":\"2025-08-07 14:30:00\""));
        
        System.out.println("Timestamp serialization test: " + json);
    }
}
