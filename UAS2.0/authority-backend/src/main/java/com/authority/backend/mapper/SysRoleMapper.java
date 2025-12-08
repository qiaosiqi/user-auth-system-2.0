package com.authority.backend.mapper;

import com.authority.backend.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 * 继承 BaseMapper 自动拥有所有 CRUD 方法
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    // 暂时不需要自定义方法
}