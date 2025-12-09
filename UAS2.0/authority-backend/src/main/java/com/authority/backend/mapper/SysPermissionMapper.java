// com.authority.backend.mapper.SysPermissionMapper.java

package com.authority.backend.mapper;

import com.authority.backend.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper 接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    // BaseMapper 提供了 CRUD 的基本方法，无需手动实现
}