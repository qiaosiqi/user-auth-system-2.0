-- 创建数据库
CREATE DATABASE IF NOT EXISTS authority_sys DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE authority_sys;

-- 1. 系统角色表 (sys_role)
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 系统用户表 (sys_user)
-- 说明：因为定义了用户只能对应一个角色，所以直接存储 role_id
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机',
  `phone` varchar(20) DEFAULT NULL COMMENT '座机',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `homepage_url` varchar(255) DEFAULT NULL COMMENT '个人主页',
  `role_id` bigint(20) DEFAULT NULL COMMENT '所属角色ID',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  -- 认证时间相关字段
  `auth_id_time` datetime DEFAULT NULL COMMENT '身份认证时间',
  `auth_mobile_time` datetime DEFAULT NULL COMMENT '手机认证时间',
  `auth_email_time` datetime DEFAULT NULL COMMENT '邮箱认证时间',
  PRIMARY KEY (`id`),
  KEY `idx_role` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 系统功能/菜单表 (sys_menu)
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '功能ID',
  `menu_name` varchar(50) NOT NULL COMMENT '功能名称(如:用户管理)',
  `path` varchar(100) DEFAULT NULL COMMENT '前端路由路径',
  `component` varchar(100) DEFAULT NULL COMMENT '前端组件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能菜单表';

-- 4. 系统操作表 (sys_operation)
-- 定义通用的操作，如：查询、打印、新增、删除
CREATE TABLE `sys_operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `operation_name` varchar(50) NOT NULL COMMENT '操作名称(如:打印)',
  `operation_code` varchar(50) NOT NULL COMMENT '操作编码(如:print, query)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作表';

-- 5. 角色权限分配表 (sys_role_permission)
-- 核心表：描述 角色-功能-操作 的三维关系
-- 记录：角色ID为X的人，在功能ID为Y的页面，拥有操作ID为Z的权限
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '功能ID',
  `operation_id` bigint(20) NOT NULL COMMENT '操作ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限分配表';

-- 初始化一些基础数据，方便后续测试
INSERT INTO `sys_role` (`id`, `role_name`, `description`) VALUES (1, '超级管理员', '拥有所有权限');
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role_id`) VALUES ('admin', '123456', '管理员', 1);

INSERT INTO `sys_menu` (`id`, `menu_name`, `path`) VALUES (1, '用户管理', '/user'), (2, '角色管理', '/role');
INSERT INTO `sys_operation` (`id`, `operation_name`, `operation_code`) VALUES (1, '查询', 'query'), (2, '新增', 'add'), (3, '删除', 'delete');