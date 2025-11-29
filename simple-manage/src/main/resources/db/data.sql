-- ============================================
-- 初始化管理员账号
-- ============================================
-- 注意：系统已改用代码方式自动初始化数据（DataInitializer.java）
-- 此文件仅作为参考，如需手动执行请取消注释

-- INSERT INTO `sys_user` (`username`, `password`, `status`, `create_by`, `remark`)
-- VALUES
--     ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 'system', '系统默认管理员账号（不可删除）');

-- 说明：
-- 1. 系统启动时会自动创建 admin 账号（如果不存在）
-- 2. 默认用户名：admin
-- 3. 默认密码：admin123
-- 4. admin 账号根据用户名判断，不可删除（username='admin' 时禁止删除）
