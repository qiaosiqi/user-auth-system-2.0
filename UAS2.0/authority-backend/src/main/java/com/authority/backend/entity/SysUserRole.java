// com.authority.backend.entity.SysUserRole.java

package com.authority.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户-角色关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    // 此表无自增ID
    private Long userId;

    private Long roleId;
}