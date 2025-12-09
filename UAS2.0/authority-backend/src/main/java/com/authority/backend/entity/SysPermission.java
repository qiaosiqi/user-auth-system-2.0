// com.authority.backend.entity.SysPermission.java

package com.authority.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限/菜单 实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission") // 假设数据库表名为 sys_permission
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId; // 父ID

    private String functionName; // 权限/菜单名称

    private String functionCode; // 权限标识 (唯一标识符)

    private String path; // 前端路由路径

    private Integer type; // 类型 (1: 目录, 2: 菜单, 3: 按钮)

    private Integer sortNum; // 排序号

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}