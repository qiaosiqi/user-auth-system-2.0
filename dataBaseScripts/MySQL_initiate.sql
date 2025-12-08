-- 初始化脚本：用户权限管理系统
-- 此脚本将重置数据库至初始状态，所有现有数据将被清除！

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 删除已存在的数据库并重新创建
DROP DATABASE IF EXISTS `auth_system`;
CREATE DATABASE `auth_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `auth_system`;

-- 2. 创建表结构
-- 2.1 核心表
CREATE TABLE `user` (
    `user_id` BIGINT AUTO_INCREMENT COMMENT '用户ID（主键）',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '用户密码（明文）',
    `phone` VARCHAR(20) COMMENT '用户手机号',
    `status` TINYINT DEFAULT 1 COMMENT '用户激活状态（1-激活，0-禁用）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE `role` (
    `role_id` BIGINT AUTO_INCREMENT COMMENT '角色ID（主键）',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名',
    `description` TEXT COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '角色激活状态（1-激活，0-禁用）',
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB COMMENT='角色表';

CREATE TABLE `permission` (
    `permission_id` BIGINT AUTO_INCREMENT COMMENT '权限ID（主键）',
    `permission_type` VARCHAR(50) NOT NULL COMMENT '权限类型',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB COMMENT='权限表';

CREATE TABLE `menu` (
    `menu_id` BIGINT AUTO_INCREMENT COMMENT '菜单ID（主键）',
    `title` VARCHAR(50) NOT NULL COMMENT '菜单名',
    `path` VARCHAR(200) COMMENT '路由路径',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父菜单ID',
    `order_num` INT DEFAULT 0 COMMENT '排序号',
    `icon` VARCHAR(100) COMMENT '图标',
    PRIMARY KEY (`menu_id`),
    FOREIGN KEY (`parent_id`) REFERENCES `menu`(`menu_id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='菜单表';

-- 2.2 关联关系表
CREATE TABLE `user_role` (
    `id` BIGINT AUTO_INCREMENT COMMENT '关联ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `role_id` BIGINT NOT NULL COMMENT '关联角色ID',
    `assigned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户-角色关联表';

CREATE TABLE `role_permission` (
    `id` BIGINT AUTO_INCREMENT COMMENT '关联ID',
    `role_id` BIGINT NOT NULL COMMENT '关联角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '关联权限ID',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `permission`(`permission_id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB COMMENT='角色-权限关联表';

CREATE TABLE `role_menu` (
    `id` BIGINT AUTO_INCREMENT COMMENT '关联ID',
    `role_id` BIGINT NOT NULL COMMENT '关联角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '关联菜单ID',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`) ON DELETE CASCADE,
    FOREIGN KEY (`menu_id`) REFERENCES `menu`(`menu_id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB COMMENT='角色-菜单关联表';

-- 3. 插入初始数据
-- 3.1 插入菜单数据 (构建层级菜单)
INSERT INTO `menu` (`title`, `path`, `parent_id`, `order_num`, `icon`) VALUES
('系统管理', '/system', NULL, 1, 'setting'),
('业务管理', '/business', NULL, 2, 'appstore'),
('用户管理', '/user', 1, 1, 'user'),
('角色管理', '/role', 1, 2, 'team'),
('数据查询', '/query', 2, 1, 'search'),
('数据报表', '/report', 2, 2, 'bar-chart');

-- 3.2 插入权限数据
INSERT INTO `permission` (`permission_type`, `name`) VALUES
('系统管理', '用户管理'),
('系统管理', '角色管理'),
('业务操作', '数据查询'),
('业务操作', '数据编辑'),
('业务操作', '数据删除');

-- 3.3 插入角色数据
INSERT INTO `role` (`name`, `description`, `status`) VALUES
('系统管理员', '拥有系统所有权限', 1),
('普通用户', '拥有部分业务权限', 1),
('访客', '只读权限', 1);

-- 3.4 插入用户数据 (密码为明文)
INSERT INTO `user` (`username`, `password`, `phone`, `status`) VALUES
('admin', 'admin123', '13800138001', 1),
('zhangsan', 'zhangsan123', '13800138002', 1),
('lisi', 'lisi123', '13800138003', 0);

-- 3.5 建立角色与权限的关联
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), -- 系统管理员拥有所有权限
(2, 3), (2, 4),                         -- 普通用户拥有数据查询和编辑权限
(3, 3);                                 -- 访客只有数据查询权限

-- 3.6 建立角色与菜单的关联
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), -- 系统管理员可以看到所有菜单
(2, 2), (2, 5), (2, 6),                        -- 普通用户可以看到业务管理相关菜单
(3, 2), (3, 5);                                -- 访客只能看到业务管理和数据查询菜单

-- 3.7 建立用户与角色的关联
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin 是系统管理员
(2, 2), -- zhangsan 是普通用户
(3, 3); -- lisi 是访客

-- 4. 创建索引优化查询
CREATE INDEX `idx_user_status` ON `user` (`status`);
CREATE INDEX `idx_role_status` ON `role` (`status`);
CREATE INDEX `idx_menu_parent` ON `menu` (`parent_id`);
CREATE INDEX `idx_user_role` ON `user_role` (`user_id`, `role_id`);
CREATE INDEX `idx_role_permission` ON `role_permission` (`role_id`, `permission_id`);

SET FOREIGN_KEY_CHECKS = 1;

-- 完成提示
SELECT '✅ 用户权限系统数据库初始化完成！' AS '执行结果';
SELECT '   数据库: auth_system' AS '信息';
SELECT '   示例用户: admin (密码: admin123), zhangsan, lisi' AS '信息';
SELECT '   角色: 系统管理员, 普通用户, 访客' AS '信息';