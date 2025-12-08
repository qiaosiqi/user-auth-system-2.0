package com.authority.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统功能菜单实体类，对应 sys_function 表
 */
@Data
@TableName("sys_function")
public class SysFunction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId; // 父级ID，用于构建树形结构，0表示根目录

    private String functionName; // 功能菜单名称

    private String functionCode; // 权限标识（如：sys:user:view）

    private String url; // 菜单对应的前端路由路径

    private Integer type; // 类型：1-菜单，2-按钮，3-页面

    private Integer sort; // 排序字段
}