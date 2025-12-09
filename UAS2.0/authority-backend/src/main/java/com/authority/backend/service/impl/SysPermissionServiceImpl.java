// com.authority.backend.service.impl.SysPermissionServiceImpl.java

package com.authority.backend.service.impl;

import com.authority.backend.entity.SysPermission;
import com.authority.backend.mapper.SysPermissionMapper;
import com.authority.backend.service.SysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 权限 业务逻辑实现类
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    // ServiceImpl 已经自动注入了 BaseMapper，并实现了 IService 的所有方法。
    // 如果需要自定义业务逻辑，可以在这里添加。

    @Override
    @Transactional // 🚨 确保删除操作的原子性
    public boolean removePermissionAndChildren(Long permissionId) {
        if (permissionId == null) {
            return false;
        }

        // 1. 递归查找所有需要删除的ID
        List<Long> idList = findChildrenIds(permissionId);
        idList.add(permissionId); // 将自身也加入删除列表

        // 2. 执行批量删除
        if (idList.isEmpty()) {
            return true; // 如果没有找到子节点，返回成功
        }

        // 🚨 使用 Mybatis-Plus 批量删除
        return this.removeByIds(idList);
    }

    /**
     * 递归查找指定父权限ID下的所有子权限ID
     * @param parentId 父权限ID
     * @return 包含所有子节点ID的列表
     */
    private List<Long> findChildrenIds(Long parentId) {
        // 查询直接子节点
        QueryWrapper<SysPermission> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<SysPermission> children = this.list(wrapper);

        List<Long> resultIds = new java.util.ArrayList<>();
        if (children != null) {
            for (SysPermission child : children) {
                resultIds.add(child.getId());
                // 递归调用，查找子节点的子节点
                resultIds.addAll(findChildrenIds(child.getId()));
            }
        }
        return resultIds;
    }

}