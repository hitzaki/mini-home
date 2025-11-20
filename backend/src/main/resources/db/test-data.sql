-- Mini Home 测试数据
-- 注意：使用前请先运行 PasswordGenerator 生成加密密码，然后替换下面的密码字段

-- ============================================
-- 1. 用户数据
-- ============================================
-- 测试用户1：admin
-- 密码：123456（请使用PasswordGenerator生成加密密码后替换）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `device_type`, `device_id`, `create_time`, `update_time`, `deleted`) VALUES
('admin', '$2a$10$请使用PasswordGenerator生成密码后替换', '管理员', 'admin@example.com', 'PC', 'PC-001', NOW(), NOW(), 0);

-- 测试用户2：testuser
-- 密码：123456（请使用PasswordGenerator生成加密密码后替换）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `device_type`, `device_id`, `create_time`, `update_time`, `deleted`) VALUES
('testuser', '$2a$10$请使用PasswordGenerator生成密码后替换', '测试用户', 'test@example.com', 'PC', 'PC-002', NOW(), NOW(), 0);

-- ============================================
-- 2. 用户设置数据
-- ============================================
-- 假设用户ID为1（admin用户）
INSERT INTO `user_settings` (`user_id`, `device_type`, `theme`, `wallpaper`, `default_open_type`, `recycle_bin_auto_clean_days`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'PC', 'default', NULL, 'INTERNAL', 30, NOW(), NOW(), 0),
(1, 'MOBILE', 'default', NULL, 'EXTERNAL', 30, NOW(), NOW(), 0);

-- 假设用户ID为2（testuser用户）
INSERT INTO `user_settings` (`user_id`, `device_type`, `theme`, `wallpaper`, `default_open_type`, `recycle_bin_auto_clean_days`, `create_time`, `update_time`, `deleted`) VALUES
(2, 'PC', 'dark', NULL, 'INTERNAL', 30, NOW(), NOW(), 0);

-- ============================================
-- 3. 应用数据（用户ID=1，admin用户）
-- ============================================
INSERT INTO `app` (`user_id`, `name`, `icon`, `type`, `url`, `open_type`, `show_on_desktop`, `position_x`, `position_y`, `folder_id`, `sort_order`, `is_new`, `create_time`, `update_time`, `deleted`) VALUES
-- 常用网站应用
(1, 'GitHub', 'https://github.com/favicon.ico', 'WEB', 'https://github.com', 'INTERNAL', 1, 50, 50, NULL, 1, 0, NOW(), NOW(), 0),
(1, 'Google', 'https://www.google.com/favicon.ico', 'WEB', 'https://www.google.com', 'EXTERNAL', 1, 150, 50, NULL, 2, 0, NOW(), NOW(), 0),
(1, '百度', 'https://www.baidu.com/favicon.ico', 'WEB', 'https://www.baidu.com', 'INTERNAL', 1, 250, 50, NULL, 3, 0, NOW(), NOW(), 0),
(1, 'B站', 'https://www.bilibili.com/favicon.ico', 'WEB', 'https://www.bilibili.com', 'INTERNAL', 1, 350, 50, NULL, 4, 0, NOW(), NOW(), 0),
(1, 'YouTube', 'https://www.youtube.com/favicon.ico', 'WEB', 'https://www.youtube.com', 'EXTERNAL', 1, 450, 50, NULL, 5, 0, NOW(), NOW(), 0),
(1, 'ChatGPT', 'https://chat.openai.com/favicon.ico', 'WEB', 'https://chat.openai.com', 'INTERNAL', 1, 550, 50, NULL, 6, 0, NOW(), NOW(), 0),
(1, 'Gmail', 'https://mail.google.com/favicon.ico', 'WEB', 'https://mail.google.com', 'INTERNAL', 1, 50, 150, NULL, 7, 0, NOW(), NOW(), 0),
(1, 'Notion', 'https://www.notion.so/favicon.ico', 'WEB', 'https://www.notion.so', 'INTERNAL', 1, 150, 150, NULL, 8, 0, NOW(), NOW(), 0),
-- 一些不在桌面的应用（用于测试应用列表功能）
(1, 'Twitter', 'https://twitter.com/favicon.ico', 'WEB', 'https://twitter.com', 'EXTERNAL', 0, NULL, NULL, NULL, 9, 0, NOW(), NOW(), 0),
(1, 'Facebook', 'https://www.facebook.com/favicon.ico', 'WEB', 'https://www.facebook.com', 'EXTERNAL', 0, NULL, NULL, NULL, 10, 0, NOW(), NOW(), 0);

-- 应用数据（用户ID=2，testuser用户）
INSERT INTO `app` (`user_id`, `name`, `icon`, `type`, `url`, `open_type`, `show_on_desktop`, `position_x`, `position_y`, `folder_id`, `sort_order`, `is_new`, `create_time`, `update_time`, `deleted`) VALUES
(2, 'GitHub', 'https://github.com/favicon.ico', 'WEB', 'https://github.com', 'INTERNAL', 1, 50, 50, NULL, 1, 0, NOW(), NOW(), 0),
(2, 'Google', 'https://www.google.com/favicon.ico', 'WEB', 'https://www.google.com', 'INTERNAL', 1, 150, 50, NULL, 2, 0, NOW(), NOW(), 0),
(2, '百度', 'https://www.baidu.com/favicon.ico', 'WEB', 'https://www.baidu.com', 'INTERNAL', 1, 250, 50, NULL, 3, 1, NOW(), NOW(), 0); -- 标记为新应用

-- ============================================
-- 4. 桌面小组件数据
-- ============================================
-- 用户1的时钟组件
INSERT INTO `widget` (`user_id`, `type`, `config`, `position_x`, `position_y`, `width`, `height`, `sort_order`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'CLOCK', '{"format":"24h","showDate":true}', 20, 20, 200, 150, 1, NOW(), NOW(), 0);

-- 用户1的日历组件
INSERT INTO `widget` (`user_id`, `type`, `config`, `position_x`, `position_y`, `width`, `height`, `sort_order`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'CALENDAR', '{"view":"month"}', 240, 20, 300, 300, 2, NOW(), NOW(), 0);

-- ============================================
-- 5. 回收站测试数据（可选）
-- ============================================
-- 一个已删除的应用示例（用户1）
INSERT INTO `recycle_bin` (`user_id`, `item_type`, `item_id`, `item_data`, `delete_time`, `auto_clean_time`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'APP', 999, '{"id":999,"name":"已删除的应用","type":"WEB","url":"https://example.com"}', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), NOW(), NOW(), 0);

-- ============================================
-- 使用说明：
-- ============================================
-- 1. 首先运行 PasswordGenerator.java 生成加密密码
-- 2. 将生成的加密密码替换上面用户数据中的密码字段
-- 3. 执行此SQL文件插入测试数据
-- 4. 注意：用户ID是自增的，如果数据库已有数据，请调整用户ID
-- 5. 应用数据中的用户ID需要与实际插入的用户ID对应
-- ============================================

