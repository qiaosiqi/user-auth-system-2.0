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
    @Override
    public Map<String, Object> findPage(Integer pageNum, Integer pageSize, String username) {

        // 1. 创建分页对象
        // Mybatis-Plus 的 Page 对象，用于传递分页参数和接收结果
        Page<SysUser> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();

        // 如果用户名不为空，添加模糊查询条件
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        // 【注意】这里可以添加排序：例如按 ID 降序
        queryWrapper.orderByDesc("id");

        // 3. 执行分页查询
        // BaseMapper 的 selectPage 方法会根据 page 对象自动执行分页
        Page<SysUser> userPage = sysUserMapper.selectPage(page, queryWrapper);

        // 4. 封装结果
        Map<String, Object> result = new HashMap<>();

        // 将查询到的用户列表放入 map
        result.put("list", userPage.getRecords());

        // 将总记录数放入 map
        result.put("total", userPage.getTotal());

        return result;
    }

    /**
     * 实现用户分配角色的业务逻辑
     * 1. 删除用户原有的角色
     * 2. 插入用户新的角色
     */
    @Override
    @Transactional // 🚨 保证删除和插入操作的原子性
    public boolean assignRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return false;
        }

        // 1. 删除用户原有的角色关联记录
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        sysUserRoleService.remove(wrapper);

        // 2. 插入用户新的角色关联记录
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setRoleId(roleId);

        return sysUserRoleService.save(sysUserRole); // 🚨 保存新的关联记录
    }


}