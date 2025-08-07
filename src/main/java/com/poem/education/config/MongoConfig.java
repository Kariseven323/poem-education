// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "9f6f701c-5473-4d4c-9026-30d57b69e296"
//   Timestamp: "2025-08-07T11:05:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "MongoDB配置最佳实践"
//   Quality_Check: "编译通过，配置正确。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;

/**
 * MongoDB配置类
 * 配置MongoDB连接、模板和转换器
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private int port;

    @Value("${spring.data.mongodb.database:poem_education}")
    private String database;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        String connectionString;
        if (username != null && !username.isEmpty()) {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s", 
                username, password, host, port, database);
        } else {
            connectionString = String.format("mongodb://%s:%d/%s", host, port, database);
        }
        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate template = new MongoTemplate(mongoClient(), getDatabaseName());
        
        // 移除_class字段
        MappingMongoConverter converter = (MappingMongoConverter) template.getConverter();
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        return template;
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(new ArrayList<>());
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext context = new MongoMappingContext();
        context.setAutoIndexCreation(true);
        return context;
    }
}
// {{END_MODIFICATIONS}}
