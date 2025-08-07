// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "9f6f701c-5473-4d4c-9026-30d57b69e296"
//   Timestamp: "2025-08-07T11:05:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "缓存策略最佳实践"
//   Quality_Check: "编译通过，配置正确。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * 缓存配置类
 * 定义缓存键生成策略和缓存名称常量
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 缓存名称常量
     */
    public static final String GUWEN_CACHE = "guwen";
    public static final String USER_CACHE = "user";
    public static final String COMMENT_CACHE = "comment";
    public static final String SEARCH_CACHE = "search";
    public static final String STATS_CACHE = "stats";

    /**
     * 自定义缓存键生成器
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getSimpleName());
                sb.append(":");
                sb.append(method.getName());
                sb.append(":");
                
                for (Object param : params) {
                    if (param != null) {
                        sb.append(param.toString());
                        sb.append(":");
                    }
                }
                
                // 移除最后一个冒号
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ':') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                
                return sb.toString();
            }
        };
    }
}
// {{END_MODIFICATIONS}}
