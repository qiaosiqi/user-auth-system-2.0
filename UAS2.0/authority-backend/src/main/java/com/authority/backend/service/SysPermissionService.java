// com.authority.backend.service.SysPermissionService.java

package com.authority.backend.service;

import com.authority.backend.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 权限 业务逻辑接口
 */
public interface SysPermissionService extends IService<SysPermission> {

    // IService 继承了所有基本 Service 方法，例如 list()

    /**
     * 删除权限及其所有子权限
     * @param permissionId 权限ID
     * @return 成功或失败
     */
    boolean removePermissionAndChildren(Long permissionId); // 🚨 新增方法声明
}