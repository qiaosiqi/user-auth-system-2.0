package com.authority.backend.controller;

// 导入 SysRoleFunction 实体类 (解决 SysRoleFunction 标红)
import com.authority.backend.entity.SysRoleFunction;

// 导入 QueryWrapper 类 (解决 queryWrapper 标红)
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.authority.backend.entity.SysRole;
import com.authority.backend.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.authority.backend.service.SysRoleFunctionService; // 新增导入
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper; // 新增导入
import java.util.stream.Collectors; // 新增导入

/**
 * 系统角色接口
 */
@RestController
@RequestMapping("/api/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired // 注入新的关联 Service
    private SysRoleFunctionService sysRoleFunctionService;

    // --- 增 ---
    /**
     * 新增或修改角色
     * @param sysRole 角色实体 (ID为空则新增，ID不为空则修改)
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody SysRole sysRole) {
        Map<String, Object> result = new HashMap<>();

        // 使用 IService.saveOrUpdate 方法，根据ID是否存在自动判断新增或修改
        if (sysRoleService.saveOrUpdate(sysRole)) {
            result.put("code", 200);
            result.put("msg", "操作成功");
        } else {
            result.put("code", 500);
            result.put("msg", "操作失败");
        }
        return result;
    }

    // --- 删 ---
    /**
     * 根据ID删除角色
     * @param id 角色ID
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        // 实际项目中，删除角色前需要检查是否有用户关联该角色！这里暂时简化。
        if (sysRoleService.removeById(id)) {
            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            result.put("code", 500);
            result.put("msg", "删除失败，角色不存在");
        }
        return result;
    }

    // --- 查 ---
    /**
     * 查询所有角色列表
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();

        List<SysRole> roles = sysRoleService.list();

        result.put("code", 200);
        result.put("data", roles);
        result.put("msg", "查询成功");
        return result;
    }

    // --- 查 ---
    /**
     * 根据名称模糊查询角色列表 (查询/分页功能的基础)
     * @param name 角色名称关键字
     */
    @GetMapping("/queryByName")
    public Map<String, Object> queryByName(@RequestParam(required = false) String name) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        // 如果 name 不为空，则添加模糊查询条件
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("role_name", name);
        }

        List<SysRole> roles = sysRoleService.list(queryWrapper);

        result.put("code", 200);
        result.put("data", roles);
        result.put("msg", "查询成功");
        return result;
    }

    // ... (保留原有的 save, delete, list 接口)

    // --- 权限分配接口 ---
    /**
     * 为角色分配功能权限
     * 请求体格式：{"roleId": 1, "functionIds": [1, 2, 3]}
     */
    @PostMapping("/assignFunction")
    public Map<String, Object> assignFunction(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取参数
        Long roleId = ((Number) params.get("roleId")).longValue();
        // 因为 JSON 数组会被 Spring 解析为 List<Integer>，需要转换
        List<Long> functionIds = ((List<?>) params.get("functionIds")).stream()
                .map(id -> ((Number) id).longValue())
                .collect(Collectors.toList());

        // 2. 调用Service层进行分配操作（删除旧的，批量新增新的）
        if (sysRoleFunctionService.assignFunctions(roleId, functionIds)) {
            result.put("code", 200);
            result.put("msg", "权限分配成功");
        } else {
            result.put("code", 500);
            result.put("msg", "权限分配失败");
        }
        return result;
    }

    // --- 查询权限接口 ---
    /**
     * 根据角色ID查询已拥有的功能ID列表
     * @param roleId 角色ID
     */
    @GetMapping("/getFunctionsByRole/{roleId}")
    public Map<String, Object> getFunctionsByRole(@PathVariable Long roleId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询关联表中所有属于该角色的记录
        QueryWrapper<SysRoleFunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<SysRoleFunction> list = sysRoleFunctionService.list(queryWrapper);

        // 2. 提取出所有的 functionId
        List<Long> functionIds = list.stream()
                .map(SysRoleFunction::getFunctionId)
                .collect(Collectors.toList());

        result.put("code", 200);
        result.put("data", functionIds);
        result.put("msg", "查询成功");
        return result;
    }

}

