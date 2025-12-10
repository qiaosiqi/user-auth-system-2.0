// com.authority.backend.config.WebMvcConfig.java

package com.authority.backend.config;

import com.authority.backend.interceptor.AuthInterceptor;
import com.authority.backend.service.SysUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 🚨 新增导入

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private SysUserService sysUserService;

    // =======================================================
    // 🚨 核心修正：添加 CORS 跨域配置
    // =======================================================
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许对所有接口进行跨域访问
                .allowedOriginPatterns("http://localhost:5174", "http://127.0.0.1:5174") // 允许前端的域名和端口
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必须包含 OPTIONS
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送 Cookie
                .maxAge(3600); // 预检请求的缓存时间
    }


    // =======================================================
    // 权限拦截器配置 (保持不变)
    // =======================================================
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册权限认证拦截器
        // 注意：这里最好使用注入的 Bean，而不是 new AuthInterceptor(...)
        // 确保你的 AuthInterceptor 是一个 @Component
        registry.addInterceptor(authInterceptor) // 🚨 使用注入的 Bean
                // **拦截所有路径**
                .addPathPatterns("/**")
                // **排除不需要拦截的路径 (白名单)**
                .excludePathPatterns("/api/user/login") // 登录接口必须放行
                .excludePathPatterns("/error")           // 错误路径放行
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }
}