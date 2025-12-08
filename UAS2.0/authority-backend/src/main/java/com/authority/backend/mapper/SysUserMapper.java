package com.authority.backend.mapper;

import com.authority.backend.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户表 Mapper 接口
 * 继承 BaseMapper<SysUser> 后，自动拥有了针对 SysUser 表的 CRUD 方法
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 这里目前什么都不用写，MyBatis-Plus 已经帮你把 SQL 都在底层写好了

    /**
     * 多表关联查询用户的权限代码
     */
    List<String> selectPermissionsByUserId(Long userId);
}