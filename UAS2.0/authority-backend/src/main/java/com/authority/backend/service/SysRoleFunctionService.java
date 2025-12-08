package com.authority.backend.service;

import com.authority.backend.entity.SysRoleFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleFunctionService extends IService<SysRoleFunction> {

    /**
     * 权限分配的核心逻辑：先删除角色的旧权限，再批量新增新权限
     * @param roleId 角色ID
     * @param functionIds 功能ID列表
     * @return 是否操作成功
     */
    boolean assignFunctions(Long roleId, List<Long> functionIds);
}