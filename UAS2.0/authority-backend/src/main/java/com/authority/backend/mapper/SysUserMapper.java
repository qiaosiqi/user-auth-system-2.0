package com.authority.backend.mapper;

import com.authority.backend.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    /**
     * 🚨 新增：联表查询用户列表（带分页），并返回角色名
     * @param page Mybatis-Plus 分页对象
     * @param username 用户名查询条件
     * @return 包含用户数据和角色名的 Map 列表
     */
    Page<Map<String, Object>> selectUserPage(Page<Map<String, Object>> page, @Param("username") String username);
}