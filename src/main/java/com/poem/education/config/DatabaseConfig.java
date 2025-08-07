// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "9f6f701c-5473-4d4c-9026-30d57b69e296"
//   Timestamp: "2025-08-07T11:05:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "多数据源配置最佳实践"
//   Quality_Check: "编译通过，配置正确。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库配置类
 * 配置MySQL主数据源、MongoDB数据源和事务管理
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.poem.education.repository.mysql",
    entityManagerFactoryRef = "mysqlEntityManagerFactory",
    transactionManagerRef = "mysqlTransactionManager"
)
@EnableMongoRepositories(
    basePackages = "com.poem.education.repository.mongodb"
)
public class DatabaseConfig {

    /**
     * MySQL主数据源配置
     */
    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * MySQL EntityManagerFactory配置
     */
    @Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.poem.education.entity.mysql");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
        jpaProperties.put("hibernate.show_sql", "false");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.use_sql_comments", "true");
        jpaProperties.put("hibernate.jdbc.batch_size", "20");
        jpaProperties.put("hibernate.jdbc.fetch_size", "50");
        jpaProperties.put("hibernate.cache.use_second_level_cache", "false");
        jpaProperties.put("hibernate.cache.use_query_cache", "false");
        
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    /**
     * MySQL事务管理器配置
     */
    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
// {{END_MODIFICATIONS}}
