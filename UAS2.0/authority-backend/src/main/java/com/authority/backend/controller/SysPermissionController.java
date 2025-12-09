package com.authority.backend.controller;

import com.authority.backend.entity.SysPermission;
import com.authority.backend.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增或编辑权限节点
     * POST /api/permission/saveOrUpdate
     */
    @PostMapping("/saveOrUpdate")
    public Map<String, Object> saveOrUpdate(@RequestBody SysPermission sysPermission) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = sysPermissionService.saveOrUpdate(sysPermission);
            if (success) {
                result.put("code", 200);
                result.put("msg", "权限保存成功");
            } else {
                result.put("code", 500);
                result.put("msg", "权限保存失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "保存失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 递归删除权限及其子权限
     * DELETE /api/permission/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deletePermission(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 🚨 调用 Service 层实现的递归删除方法
            boolean success = sysPermissionService.removePermissionAndChildren(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "权限及其子权限删除成功");
            } else {
                result.put("code", 500);
                result.put("msg", "权限删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除失败：" + e.getMessage());
        }
        return result;
    }

}