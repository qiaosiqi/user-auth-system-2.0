package com.authority.backend.controller;

import com.authority.backend.entity.SysUser;
import com.authority.backend.service.SysUserService;
import com.authority.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest; // 【新增或替换】这个导入语句
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户接口控制器
 */
@RestController // 标记这是一个 RESTful API 控制器
@RequestMapping("/api/user") // 设定基础路径
public class SysUserController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    public SysUserController(SysUserService sysUserService, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.jwtUtil = jwtUtil;
    }

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
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

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
     * 用户列表接口：/api/user/list
     * GET 请求，支持分页和用户名查询
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            // 🚨 接收前端传递的分页和查询参数
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {

        // 假设 SysUserService 提供了 findPage 方法来处理业务逻辑
        // 并返回一个 Map，其中包含 "list" (数据列表) 和 "total" (总数)
        Map<String, Object> pageData = sysUserService.findPage(pageNum, pageSize, username);

        Map<String, Object> result = new HashMap<>();

        result.put("code", 200);
        result.put("msg", "查询成功");

        // 🚨 返回给前端的数据结构必须包含 list 和 total
        result.put("data", pageData);

        // 如果你的 findPage 方法返回的 Map 中就是 list 和 total，则直接返回：
        // return pageData;

        return result;
    }


    /**
     * 删除用户接口：/api/user/delete/{userId}
     * DELETE 请求
     */
    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> delete(@PathVariable Long userId) {

        // 1. 调用 Service 层进行删除操作
        boolean success = sysUserService.removeById(userId); // Mybatis-Plus 内置的删除方法

        Map<String, Object> result = new HashMap<>();

        if (success) {
            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            // 删除失败，可能是用户不存在
            result.put("code", 500);
            result.put("msg", "删除失败，用户可能不存在");
        }
        return result;
    }

    /**
     * 新增或编辑用户接口：/api/user/saveOrUpdate
     * POST 请求
     * 接收一个 SysUser 实体作为请求体
     */
    @PostMapping("/saveOrUpdate")
    public Map<String, Object> saveOrUpdate(@RequestBody SysUser sysUser) {

        // **MyBatis-Plus 核心功能:**
        // 1. 如果 sysUser.getId() 不为空，MyBatis-Plus 会自动执行更新 (UPDATE) 操作。
        // 2. 如果 sysUser.getId() 为空，MyBatis-Plus 会自动执行插入 (INSERT) 操作。
        boolean success = sysUserService.saveOrUpdate(sysUser);

        Map<String, Object> result = new HashMap<>();

        if (success) {
            result.put("code", 200);
            // 根据是否有ID来判断是新增还是更新，返回更准确的消息
            result.put("msg", (sysUser.getId() != null ? "更新" : "新增") + "用户成功");
        } else {
            result.put("code", 500);
            result.put("msg", (sysUser.getId() != null ? "更新" : "新增") + "用户失败");
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

    /**
     * 为用户分配角色
     * POST /api/user/assignRole
     * 请求体格式：{"userId": 1, "roleId": 2}
     */
    @PostMapping("/assignRole")
    public Map<String, Object> assignRole(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从 Map 中获取 Long 类型的 userId 和 roleId
            Long userId = ((Number) params.get("userId")).longValue();
            Long roleId = ((Number) params.get("roleId")).longValue();

            // 调用 Service 层处理分配逻辑
            boolean success = sysUserService.assignRole(userId, roleId);

            if (success) {
                result.put("code", 200);
                result.put("msg", "角色分配成功");
            } else {
                result.put("code", 500);
                result.put("msg", "角色分配失败");
            }
        } catch (Exception e) {
            // 捕获异常，比如参数转换失败
            result.put("code", 500);
            result.put("msg", "参数错误或处理失败: " + e.getMessage());
        }
        return result;
    }

}
