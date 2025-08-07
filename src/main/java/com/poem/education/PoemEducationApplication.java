// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "46947b2e-3254-4ef7-a17e-0a2c73569f7a"
//   Timestamp: "2025-08-07T10:50:34+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Boot主启动类标准实现"
//   Quality_Check: "编译通过，注解配置正确。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 诗词交流鉴赏平台主启动类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.poem.education.repository.mysql")
@EnableMongoRepositories(basePackages = "com.poem.education.repository.mongodb")
@EnableCaching
@EnableTransactionManagement
public class PoemEducationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoemEducationApplication.class, args);
    }
}
// {{END_MODIFICATIONS}}
