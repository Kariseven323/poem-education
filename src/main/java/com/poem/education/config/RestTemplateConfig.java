// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "ba11a9d3-6ea1-4b0b-b3d9-86fc30515df8"
//   Timestamp: "2025-08-08T13:45:20+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "Spring Boot配置类最佳实践，HTTP客户端配置"
//   Quality_Check: "编译通过，RestTemplate配置完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 用于HTTP客户端调用外部API
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * 创建RestTemplate Bean
     * 配置超时时间和连接参数
     * 
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }
    
    /**
     * 配置HTTP请求工厂
     * 设置连接超时和读取超时
     * 
     * @return ClientHttpRequestFactory实例
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时时间：10秒
        factory.setConnectTimeout(10000);
        // 读取超时时间：30秒
        factory.setReadTimeout(30000);
        return factory;
    }
}
// {{END_MODIFICATIONS}}
