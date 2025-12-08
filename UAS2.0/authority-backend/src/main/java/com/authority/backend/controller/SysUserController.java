package com.authority.backend.controller;

import com.authority.backend.entity.SysUser;
import com.authority.backend.service.SysUserService;
import com.authority.backend.utils.JwtUtil; // 【新增导入】
import jakarta.servlet.http.HttpServletRequest; // 【新增或替换】这个导入语句
import org.springframework.web.bind.annotation.GetMapping; // 注意：你新增 testAuth 接口可能需要这个导入
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;


/**
 * 用户接口控制器
 */
@RestController // 标记这是一个 RESTful API 控制器
@RequestMapping("/api/user") // 设定基础路径
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录接口：/api/user/login
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {

        Map<String, Object> result = new HashMap<>();

        SysUser user = sysUserService.login(username, password);

        if (user != null) {
            // 登录成功

            // 1. 生成 JWT Token
            String token = JwtUtil.generateToken(user.getId(), user.getUsername());

            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token); // 【重要】返回 Token 给前端

            // 可以返回用户基本信息，但不包含敏感信息
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getId());
            userData.put("username", user.getUsername());
            userData.put("roleId", user.getRoleId());
            result.put("userInfo", userData);

        } else {
            // 登录失败
            result.put("code", 401);
            result.put("msg", "账号或密码错误");
            result.put("token", null);
        }
        return result;
    }

    /**
     * 测试接口：需要 Token 才能访问
     */
    @GetMapping("/testAuth")
    public Map<String, Object> testAuth(HttpServletRequest request) {

        // 【修正点】安全地获取 Long 类型的 userId
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;

        if (userIdObj instanceof Integer) {
            // 如果是 Integer 类型，先转为 String 再转 Long
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            // 如果已经是 Long 类型，直接转换
            userId = (Long) userIdObj;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "权限验证通过，可以访问");
        result.put("currentUserId", userId);
        return result;
    }
}