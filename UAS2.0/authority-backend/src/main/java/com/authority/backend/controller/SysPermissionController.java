package com.authority.backend.controller;

import com.authority.backend.entity.SysPermission;
import com.authority.backend.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService; // 🚨 确保 Service 类名为 SysPermissionService

    /**
     * 获取所有权限列表
     * 接口：GET /api/permission/list
     */
    @GetMapping("/list")
    public Map<String, Object> list() {

        // 假设 SysPermissionService 实现了 Mybatis-Plus 的 BaseService，
        // 可以直接调用 list() 方法获取所有权限。
        List<SysPermission> permissionList = sysPermissionService.list();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "查询所有权限成功");
        result.put("data", permissionList);
        return result;
    }
}