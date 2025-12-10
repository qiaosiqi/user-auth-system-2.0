package com.authority.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 核心应用配置类
 */
@Configuration
public class AppConfig {

    /**
     * 注册 RestTemplate Bean，用于调用外部 RESTful API (如 DeepSeek)
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}