-- Mini Home 数据库表结构

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(500) COMMENT '头像URL',
  `email` VARCHAR(100) COMMENT '邮箱',
  `device_type` VARCHAR(10) COMMENT '设备类型：PC/MOBILE',
  `device_id` VARCHAR(100) COMMENT '设备标识',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 应用表
CREATE TABLE IF NOT EXISTS `app` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '应用名称',
  `icon` VARCHAR(500) COMMENT '应用图标URL',
  `type` VARCHAR(20) NOT NULL DEFAULT 'WEB' COMMENT '应用类型：WEB/NATIVE',
  `url` VARCHAR(500) COMMENT '应用URL（Web应用使用）',
  `open_type` VARCHAR(20) DEFAULT 'INTERNAL' COMMENT '打开方式：EXTERNAL/INTERNAL',
  `show_on_desktop` INT DEFAULT 1 COMMENT '是否在桌面显示：0-否，1-是',
  `position_x` INT COMMENT '桌面位置X坐标',
  `position_y` INT COMMENT '桌面位置Y坐标',
  `folder_id` BIGINT COMMENT '所属文件夹ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `is_new` INT DEFAULT 0 COMMENT '是否为新应用：0-否，1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

-- 应用文件夹表
CREATE TABLE IF NOT EXISTS `app_folder` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
  `icon` VARCHAR(500) COMMENT '文件夹图标',
  `position_x` INT COMMENT '桌面位置X坐标',
  `position_y` INT COMMENT '桌面位置Y坐标',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用文件夹表';

-- 桌面小组件表
CREATE TABLE IF NOT EXISTS `widget` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '组件类型：CLOCK/CALENDAR/WEATHER/SEARCH/PERFORMANCE',
  `config` TEXT COMMENT '组件配置（JSON格式）',
  `position_x` INT COMMENT '桌面位置X坐标',
  `position_y` INT COMMENT '桌面位置Y坐标',
  `width` INT DEFAULT 200 COMMENT '宽度',
  `height` INT DEFAULT 150 COMMENT '高度',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌面小组件表';

-- 回收站表
CREATE TABLE IF NOT EXISTS `recycle_bin` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `item_type` VARCHAR(20) NOT NULL COMMENT '删除类型：APP/FOLDER/WIDGET',
  `item_id` BIGINT NOT NULL COMMENT '删除的项目ID',
  `item_data` TEXT COMMENT '删除的项目数据（JSON格式，用于恢复）',
  `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
  `auto_clean_time` DATETIME COMMENT '自动清理时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_auto_clean_time` (`auto_clean_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收站表';

-- 用户设置表
CREATE TABLE IF NOT EXISTS `user_settings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `device_type` VARCHAR(10) NOT NULL COMMENT '设备类型：PC/MOBILE',
  `theme` VARCHAR(50) DEFAULT 'default' COMMENT '主题名称',
  `wallpaper` VARCHAR(500) COMMENT '壁纸URL',
  `default_open_type` VARCHAR(20) DEFAULT 'INTERNAL' COMMENT '默认应用打开方式：EXTERNAL/INTERNAL',
  `recycle_bin_auto_clean_days` INT DEFAULT 30 COMMENT '回收站自动清理天数（0表示不自动清理）',
  `other_settings` TEXT COMMENT '其他设置（JSON格式）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  UNIQUE KEY `uk_user_device` (`user_id`, `device_type`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';

-- 应用盒子表
CREATE TABLE IF NOT EXISTS `app_box` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '盒子名称',
  `icon` VARCHAR(500) COMMENT '盒子图标',
  `position_x` INT COMMENT '桌面位置X坐标',
  `position_y` INT COMMENT '桌面位置Y坐标',
  `width` INT DEFAULT 300 COMMENT '宽度',
  `height` INT DEFAULT 400 COMMENT '高度',
  `is_expanded` INT DEFAULT 1 COMMENT '是否展开：0-否，1-是',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用盒子表';

-- 应用盒子项目表
CREATE TABLE IF NOT EXISTS `app_box_item` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `box_id` BIGINT NOT NULL COMMENT '应用盒子ID',
  `app_id` BIGINT NOT NULL COMMENT '应用ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  INDEX `idx_box_id` (`box_id`),
  INDEX `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用盒子项目表';

