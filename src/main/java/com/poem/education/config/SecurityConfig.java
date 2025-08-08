// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Security配置最佳实践"
//   Quality_Check: "编译通过，安全配置完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.poem.education.security.JwtAuthenticationFilter;

/**
 * Spring Security配置类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 密码编码器Bean
     * 使用BCrypt加密算法
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * CORS配置
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（使用JWT时不需要）
            .csrf().disable()
            
            // 启用CORS
            .cors().configurationSource(corsConfigurationSource())
            
            .and()
            
            // 配置会话管理为无状态
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            
            // 配置请求授权
            .authorizeRequests()
            // 允许认证相关接口无需认证
            .antMatchers("/api/v1/auth/**").permitAll()
            // 允许公开的用户信息接口
            .antMatchers("/api/v1/users/{userId}").permitAll()
            .antMatchers("/api/v1/users/by-username/{username}").permitAll()
            // 用户个人信息接口需要认证
            .antMatchers("/api/v1/users/profile").authenticated()
            // 允许古文相关公开接口
            .antMatchers("/api/v1/guwen/**").permitAll()
            .antMatchers("/api/v1/writers/**").permitAll()
            .antMatchers("/api/v1/sentences/**").permitAll()
            // 允许评论相关接口（查看、创建、删除、点赞等）
            .antMatchers("/api/v1/comments/**").permitAll()
            // 允许Swagger文档访问
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            // 允许健康检查
            .antMatchers("/actuator/health").permitAll()
            // 其他请求需要认证
            .anyRequest().authenticated()
            
            .and()

            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 禁用默认登录页面
            .formLogin().disable()

            // 禁用HTTP Basic认证
            .httpBasic().disable();
    }
}
// {{END_MODIFICATIONS}}
