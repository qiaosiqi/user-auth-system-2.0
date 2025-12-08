package com.authority.backend.interceptor;

import com.authority.backend.service.SysUserService; // 新增导入

import com.authority.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 权限认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    // 注入 UserService 来获取用户权限
    private final SysUserService sysUserService;

    // 构造函数注入 (需要修改 WebMvcConfig 来创建实例)
    public AuthInterceptor(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 1. 从请求头获取 Token
        String token = request.getHeader("Authorization");

        // 2. 检查 Token 是否存在且格式正确 (Bearer <token>)
        if (token == null || !token.startsWith("Bearer ")) {
            returnJson(response, 401, "请登录以获取访问权限！");
            return false;
        }

        // 3. 提取 JWT 字符串
        String jwt = token.substring(7);

        // 4. 校验 Token 有效性
        Claims claims = JwtUtil.parseToken(jwt);

        if (claims == null || !JwtUtil.validateToken(jwt)) {
            returnJson(response, 401, "Token 无效或已过期，请重新登录！");
            return false;
        }

        // 5. 将解析出的用户信息（如 userId, roleId）存入请求属性，方便后续业务逻辑使用
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.get("username"));

        // 假设 JWT 校验成功后，我们已经获取了 userId
        Long userId = claims.get("userId", Long.class);

        // --- 权限检查核心逻辑 ---

        // 1. 根据用户ID获取其所有权限代码
        List<String> permissions = sysUserService.getPermissionsByUserId(userId);

        // 2. 将权限列表存入请求属性，方便 Controller 或其他拦截器使用
        request.setAttribute("userId", userId);
        request.setAttribute("permissions", permissions); // 【新增】

        // 3. 示例：对 /api/role/delete/* 接口进行权限校验
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/role/delete")) {
            // 定义删除角色的权限代码
            String requiredPermission = "sys:role:delete";

            if (!permissions.contains(requiredPermission)) {
                // 如果用户没有该权限
                response.setStatus(403); // 403 Forbidden
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\": 403, \"msg\": \"权限不足，请联系管理员！\"}");
                return false; // 拦截请求
            }
        }

        // Token 有效，继续执行 Controller
        return true;
    }

    /**
     * 辅助方法：返回 JSON 格式的错误响应
     */
    private void returnJson(HttpServletResponse response, int status, String msg) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(status);
        // 使用简单的 JSON 格式
        String json = String.format("{\"code\":%d, \"msg\":\"%s\", \"data\":null}", status, msg);
        response.getWriter().write(json);
    }
}