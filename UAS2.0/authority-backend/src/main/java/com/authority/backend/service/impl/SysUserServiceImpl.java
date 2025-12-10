package com.authority.backend.service.impl;

import java.util.List;

import com.authority.backend.entity.SysUser;
import com.authority.backend.entity.SysUserRole;
import com.authority.backend.mapper.SysUserMapper;
import com.authority.backend.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.authority.backend.service.SysUserRoleService; // 🚨 导入新的 Service
import org.springframework.transaction.annotation.Transactional; // 🚨 导入事务注解
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.HashMap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 🚨 新增导入 Page 类
import java.util.Objects; // 🚨 确保你导入了 StringUtils，如果报错，请使用 java.util.Objects.nonNull 或手动检查


/**
 * 用户业务逻辑实现类
 */
@Service // 标记这是一个Spring Service组件
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper; // 注入Mapper，虽然ServiceImpl自带，但有时候我们仍然需要直接使用它

    @Autowired
    private SysUserRoleService sysUserRoleService; // 🚨 注入用户-角色关联 Service

    @Override
    public SysUser login(String username, String password) {
        // 1. 根据用户名查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username); // where username = #{username}

        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            // 用户不存在
            return null;
        }

        // 2. 校验密码
        // 注意：在实际项目中，密码应该进行加密（如 MD5 或 bcrypt）后再比较。
        // 这里为了简化，我们暂时使用明文比较。
        if (!user.getPassword().equals(password)) {
            // 密码错误
            return null;
        }

        // 3. 登录成功
        return user;
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        // 直接调用 Mapper 中实现的查询方法
        return sysUserMapper.selectPermissionsByUserId(userId);
    }


    /**
     * 实现 SysUserService 接口中的 findPage 抽象方法
     * 用于用户列表的分页查询
     */
    /**
     * 实现 SysUserService 接口中的 findPage 抽象方法
     * 用于用户列表的分页查询 (🚨 修改此方法，使用联表查询)
     */
    @Override
    public Map<String, Object> findPage(Integer pageNum, Integer pageSize, String username) {

        // 1. 创建分页对象 (注意：这里的泛型从 SysUser 变为了 Map)
        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);

        // 2. 🚨 调用自定义的联表查询 Mapper 方法
        // 这里不再需要手动构建 QueryWrapper，查询条件在 XML 中处理
        Page<Map<String, Object>> userPage = sysUserMapper.selectUserPage(page, username);

        // 3. 封装结果
        Map<String, Object> result = new HashMap<>();

        // 🚨 返回的是 Map 列表，其中包含了 roleName 字段
        result.put("list", userPage.getRecords());

        // 将总记录数放入 map
        result.put("total", userPage.getTotal());

        return result;
    }

    @Override
    @Transactional
    public boolean assignRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return false;
        }

        // **1. 删除用户原有的角色关联记录**
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        // 🚨 修正点 A：删除操作。如果 sys_user_role 表没有主键，
        // Mybatis-Plus 的 remove 方法可能依赖于实体自身的逻辑，我们确保删除是成功的。
        // remove(wrapper) 返回 boolean，我们忽略它，继续执行插入。

        sysUserRoleService.remove(wrapper);

        // **2. 插入用户新的角色关联记录**
        // 🚨 修正点 B：如果 roleId 传入的是 '0' 或其他特殊值，表示取消分配角色。
        if (roleId == 0L) { // 假设 0L 表示取消分配
            return true; // 成功取消分配
        }

        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setRoleId(roleId);

        // 🚨 修正点 C：检查 save 操作是否成功。
        boolean success = sysUserRoleService.save(sysUserRole);

        // 🚨 【关键新增】更新 SysUser 主表的 roleId 冗余字段（如果你在列表查询时依赖它）
        SysUser userUpdate = new SysUser();
        userUpdate.setId(userId);
        userUpdate.setRoleId(roleId);
        // this.updateById 是继承自 ServiceImpl 的方法，用于更新 SysUser
        boolean updateSuccess = this.updateById(userUpdate);

        // 只有当两个操作都成功时才返回 true
        return success && updateSuccess;
    }


}