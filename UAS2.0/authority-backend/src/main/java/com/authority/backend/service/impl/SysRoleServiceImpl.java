package com.authority.backend.service.impl;

import com.authority.backend.entity.SysRole;
import com.authority.backend.mapper.SysRoleMapper;
import com.authority.backend.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色业务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    // 继承 ServiceImpl 后，我们已经拥有了 save, remove, update, list 等所有基础 CRUD 方法！
}