package com.authority.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许对所有接口进行跨域访问
                .allowedOriginPatterns("http://localhost:5173", "http://127.0.0.1:5173") // 允许前端的域名和端口
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送 Cookie (虽然我们用 JWT，但最好开启)
                .maxAge(3600); // 预检请求的缓存时间
    }
}