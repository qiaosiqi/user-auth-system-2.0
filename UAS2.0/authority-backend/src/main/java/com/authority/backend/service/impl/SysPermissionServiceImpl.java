// com.authority.backend.service.impl.SysPermissionServiceImpl.java

package com.authority.backend.service.impl;

import com.authority.backend.entity.SysPermission;
import com.authority.backend.mapper.SysPermissionMapper;
import com.authority.backend.service.SysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 权限 业务逻辑实现类
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    // ServiceImpl 已经自动注入了 BaseMapper，并实现了 IService 的所有方法。
    // 如果需要自定义业务逻辑，可以在这里添加。
}