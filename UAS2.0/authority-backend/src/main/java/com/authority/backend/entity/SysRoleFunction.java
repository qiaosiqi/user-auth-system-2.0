package com.authority.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-功能关联表实体，对应 sys_role_function 表
 */
@Data
@TableName("sys_role_function")
public class SysRoleFunction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId; // 角色ID

    private Long functionId; // 功能ID（菜单/按钮）
}