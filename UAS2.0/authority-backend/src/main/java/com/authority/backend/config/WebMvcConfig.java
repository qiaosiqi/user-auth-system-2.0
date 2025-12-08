package com.authority.backend.config;

import com.authority.backend.interceptor.AuthInterceptor;
import com.authority.backend.service.SysUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 注意：这里可能需要将 @Resource 替换为 @Autowired，取决于你的 JDK 版本
    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private SysUserService sysUserService; // 【新增】注入 Service

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册权限认证拦截器
        registry.addInterceptor(new AuthInterceptor(sysUserService))
                // **拦截所有路径**
                .addPathPatterns("/**")
                // **排除不需要拦截的路径 (白名单)**
                .excludePathPatterns("/api/user/login") // 登录接口必须放行
                .excludePathPatterns("/error")           // 错误路径放行
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**"); // 如果你使用了 Swagger，需要放行文档路径
    }
}