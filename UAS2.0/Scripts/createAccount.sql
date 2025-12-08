-- 1. 创建新用户：auth_dev，密码设置为 'auth_dev_pwd'（请自行设置一个更复杂的密码）
-- 'localhost' 表示该用户只能从本机访问。
CREATE USER 'auth_dev'@'localhost' IDENTIFIED BY '000000';

-- 2. 授权：授予 auth_dev 用户对 authority_sys 数据库的所有操作权限（CRUD等）。
GRANT ALL PRIVILEGES ON authority_sys.* TO 'auth_dev'@'localhost';

-- 3. 刷新权限
FLUSH PRIVILEGES;