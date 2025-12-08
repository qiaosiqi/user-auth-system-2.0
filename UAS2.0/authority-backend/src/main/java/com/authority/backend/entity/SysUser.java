package com.authority.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 sys_user
 */
@Data  // Lombok注解：自动生成Getter, Setter, toString等方法，不用手动写了
@TableName("sys_user") // MyBatis-Plus注解：指定该类对应数据库中的哪张表
public class SysUser {

    // 主键ID，使用数据库自增策略
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String realName; // 数据库是 real_name，这里自动映射为驼峰命名 realName
    private String idCard;
    private String qq;
    private String wechat;
    private String address;
    private String mobile;
    private String phone;
    private String email;
    private String homepageUrl;

    private Long roleId; // 对应角色ID

    private Integer status; // 1正常 0禁用

    private LocalDateTime createdTime;

    // 认证时间相关
    private LocalDateTime authIdTime;
    private LocalDateTime authMobileTime;
    private LocalDateTime authEmailTime;
}