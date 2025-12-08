package com.authority.backend.controller;

import com.authority.backend.entity.SysFunction;
import com.authority.backend.service.SysFunctionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统功能菜单接口
 */
@RestController
@RequestMapping("/api/function")
public class SysFunctionController {

    @Autowired
    private SysFunctionService sysFunctionService;

    // --- 增 / 改 ---
    /**
     * 新增或修改功能菜单
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody SysFunction sysFunction) {
        Map<String, Object> result = new HashMap<>();

        if (sysFunctionService.saveOrUpdate(sysFunction)) {
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
     * 根据ID删除功能菜单
     * 注意：实际项目中，删除父菜单前需要检查是否有子菜单！这里暂时简化。
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (sysFunctionService.removeById(id)) {
            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            result.put("code", 500);
            result.put("msg", "删除失败，功能不存在");
        }
        return result;
    }

    // --- 查 ---
    /**
     * 查询所有功能菜单列表，并按排序字段升序返回（方便前端构建树）
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<SysFunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort"); // 按 sort 字段升序排列

        List<SysFunction> functions = sysFunctionService.list(queryWrapper);

        result.put("code", 200);
        result.put("data", functions);
        result.put("msg", "查询成功");
        return result;
    }
}