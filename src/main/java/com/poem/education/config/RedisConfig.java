// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "9f6f701c-5473-4d4c-9026-30d57b69e296"
//   Timestamp: "2025-08-07T11:05:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Redis配置最佳实践"
//   Quality_Check: "编译通过，配置正确。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.time.Duration;

/**
 * Redis配置类
 * 配置Redis连接池、序列化器和缓存管理器
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * RedisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用FastJSON2序列化器
        FastJson2RedisSerializer<Object> serializer = new FastJson2RedisSerializer<>(Object.class);

        // key采用String的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // value序列化方式采用fastjson
        template.setValueSerializer(serializer);
        // hash的value序列化方式采用fastjson
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        FastJson2RedisSerializer<Object> serializer = new FastJson2RedisSerializer<>(Object.class);
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // 默认缓存1小时
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues(); // 不缓存null值

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * FastJSON2 Redis序列化器
     */
    public static class FastJson2RedisSerializer<T> implements RedisSerializer<T> {
        
        private final Class<T> clazz;

        public FastJson2RedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(Charset.defaultCharset());
        }

        @Override
        public T deserialize(byte[] bytes) {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, Charset.defaultCharset());
            return JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType);
        }
    }
}
// {{END_MODIFICATIONS}}
