package com.authority.backend.service;

import com.authority.backend.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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


    public Map<String, Object> findPage(Integer pageNum, Integer pageSize, String username);


    /**
     * 根据用户ID查询其拥有的所有权限代码 (functionCode)
     * @param userId 用户ID
     * @return 权限代码列表，例如 ["sys:user:add", "sys:role:view"]
     */
    List<String> getPermissionsByUserId(Long userId);

    /**
     * 为用户分配单个角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 成功或失败
     */
    boolean assignRole(Long userId, Long roleId); // 🚨 新增方法声明

}