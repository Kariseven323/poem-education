package com.poem.education;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot应用程序集成测试
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@SpringBootTest
@ActiveProfiles("test")
class PoemEducationApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }

}
