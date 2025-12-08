package com.authority.backend.service.impl;

import com.authority.backend.entity.SysRoleFunction;
import com.authority.backend.mapper.SysRoleFunctionMapper;
import com.authority.backend.service.SysRoleFunctionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleFunctionServiceImpl extends ServiceImpl<SysRoleFunctionMapper, SysRoleFunction> implements SysRoleFunctionService {

    /**
     * 权限分配的核心逻辑：先删除角色的旧权限，再批量新增新权限
     * 必须使用 @Transactional 注解确保原子性
     */
    @Override
    @Transactional // 确保删除和新增是一个原子操作，要么都成功，要么都失败
    public boolean assignFunctions(Long roleId, List<Long> functionIds) {
        // 1. 先删除该角色所有的旧权限
        QueryWrapper<SysRoleFunction> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("role_id", roleId);
        remove(deleteWrapper); // 调用 ServiceImpl 的删除方法

        // 2. 如果 functionIds 为空（即清空权限），则直接返回成功
        if (functionIds == null || functionIds.isEmpty()) {
            return true;
        }

        // 3. 批量构建新的关联实体
        List<SysRoleFunction> listToSave = functionIds.stream()
                .map(functionId -> {
                    SysRoleFunction rf = new SysRoleFunction();
                    rf.setRoleId(roleId);
                    rf.setFunctionId(functionId);
                    return rf;
                })
                .collect(Collectors.toList());

        // 4. 批量保存新的权限
        return saveBatch(listToSave);
    }
}