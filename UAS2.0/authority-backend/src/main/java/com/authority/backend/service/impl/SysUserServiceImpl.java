package com.authority.backend.service.impl;

import java.util.List;

import com.authority.backend.entity.SysUser;
import com.authority.backend.mapper.SysUserMapper;
import com.authority.backend.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户业务逻辑实现类
 */
@Service // 标记这是一个Spring Service组件
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper; // 注入Mapper，虽然ServiceImpl自带，但有时候我们仍然需要直接使用它

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

}