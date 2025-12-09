// com.authority.backend.service.impl.SysUserRoleServiceImpl.java

package com.authority.backend.service.impl;

import com.authority.backend.entity.SysUserRole;
import com.authority.backend.mapper.SysUserRoleMapper;
import com.authority.backend.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户-角色关联 业务逻辑实现类
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
    // 继承 ServiceImpl 自动实现所有基础方法
}