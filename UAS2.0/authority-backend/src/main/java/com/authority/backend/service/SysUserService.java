package com.authority.backend.service;

import com.authority.backend.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户业务逻辑接口
 * 继承 IService<SysUser> 即可获得大量的单表操作方法
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录验证
     * @param username 账号
     * @param password 密码
     * @return 登录成功的用户信息
     */
    SysUser login(String username, String password);

    /**
     * 根据用户ID查询其拥有的所有权限代码 (functionCode)
     * @param userId 用户ID
     * @return 权限代码列表，例如 ["sys:user:add", "sys:role:view"]
     */
    List<String> getPermissionsByUserId(Long userId);
}