/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : poem_education

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 07/08/2025 10:47:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for content_stats
-- ----------------------------
DROP TABLE IF EXISTS `content_stats`;
CREATE TABLE `content_stats`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
  `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型：guwen/sentence/writer/creation',
  `view_count` bigint NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` bigint NULL DEFAULT 0 COMMENT '点赞次数',
  `favorite_count` bigint NULL DEFAULT 0 COMMENT '收藏次数',
  `comment_count` bigint NULL DEFAULT 0 COMMENT '评论次数',
  `share_count` bigint NULL DEFAULT 0 COMMENT '分享次数',
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_content`(`content_id` ASC, `content_type` ASC) USING BTREE,
  INDEX `idx_content_type`(`content_type` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '内容统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for learning_records
-- ----------------------------
DROP TABLE IF EXISTS `learning_records`;
CREATE TABLE `learning_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学习内容类型：guwen/sentence',
  `study_duration` int NULL DEFAULT 0 COMMENT '学习时长(秒)',
  `progress_status` tinyint NULL DEFAULT 0 COMMENT '进度状态：0未开始 1学习中 2已完成',
  `last_position` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习位置记录(JSON)',
  `study_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习笔记',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_id` ASC, `target_type` ASC) USING BTREE,
  INDEX `idx_user_progress`(`user_id` ASC, `progress_status` ASC) USING BTREE,
  INDEX `idx_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `learning_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户学习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值',
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_public` tinyint NULL DEFAULT 0 COMMENT '是否公开：1公开 0私有',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_actions
-- ----------------------------
DROP TABLE IF EXISTS `user_actions`;
CREATE TABLE `user_actions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型：guwen/creation/comment/sentence',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行为类型：like/favorite/view/share',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target_action`(`user_id` ASC, `target_id` ASC, `target_type` ASC, `action_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_id` ASC, `target_type` ASC) USING BTREE,
  INDEX `idx_action_type`(`action_type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `user_actions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_favorites`;
CREATE TABLE `user_favorites`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏类型：guwen/sentence/writer',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '收藏备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_favorite`(`user_id` ASC, `target_id` ASC, `target_type` ASC) USING BTREE,
  INDEX `idx_user_folder`(`user_id` ASC, `folder_name` ASC) USING BTREE,
  INDEX `idx_target_type`(`target_type` ASC) USING BTREE,
  CONSTRAINT `user_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名：admin/user/vip',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_name` ASC) USING BTREE,
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_stats`;
CREATE TABLE `user_stats`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `total_views` bigint NULL DEFAULT 0 COMMENT '总浏览数',
  `total_likes` bigint NULL DEFAULT 0 COMMENT '总点赞数',
  `total_favorites` bigint NULL DEFAULT 0 COMMENT '总收藏数',
  `total_comments` bigint NULL DEFAULT 0 COMMENT '总评论数',
  `total_creations` bigint NULL DEFAULT 0 COMMENT '总创作数',
  `study_days` int NULL DEFAULT 0 COMMENT '学习天数',
  `last_active_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_stats`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_stats_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for hot_contents
-- ----------------------------
DROP VIEW IF EXISTS `hot_contents`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `hot_contents` AS select `content_stats`.`content_id` AS `content_id`,`content_stats`.`content_type` AS `content_type`,`content_stats`.`view_count` AS `view_count`,`content_stats`.`like_count` AS `like_count`,`content_stats`.`favorite_count` AS `favorite_count`,`content_stats`.`comment_count` AS `comment_count`,((((`content_stats`.`view_count` * 1) + (`content_stats`.`like_count` * 3)) + (`content_stats`.`favorite_count` * 5)) + (`content_stats`.`comment_count` * 2)) AS `hot_score` from `content_stats` where (`content_stats`.`view_count` > 0) order by ((((`content_stats`.`view_count` * 1) + (`content_stats`.`like_count` * 3)) + (`content_stats`.`favorite_count` * 5)) + (`content_stats`.`comment_count` * 2)) desc;

-- ----------------------------
-- View structure for user_details
-- ----------------------------
DROP VIEW IF EXISTS `user_details`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `user_details` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`email` AS `email`,`u`.`nickname` AS `nickname`,`u`.`avatar` AS `avatar`,`u`.`bio` AS `bio`,`u`.`status` AS `status`,`u`.`created_at` AS `created_at`,group_concat(`ur`.`role_name` separator ',') AS `roles`,`us`.`total_views` AS `total_views`,`us`.`total_likes` AS `total_likes`,`us`.`total_favorites` AS `total_favorites`,`us`.`total_comments` AS `total_comments`,`us`.`total_creations` AS `total_creations`,`us`.`study_days` AS `study_days`,`us`.`last_active_at` AS `last_active_at` from ((`users` `u` left join `user_roles` `ur` on((`u`.`id` = `ur`.`user_id`))) left join `user_stats` `us` on((`u`.`id` = `us`.`user_id`))) group by `u`.`id`;

-- ----------------------------
-- Procedure structure for UpdateContentStats
-- ----------------------------
DROP PROCEDURE IF EXISTS `UpdateContentStats`;
delimiter ;;
CREATE PROCEDURE `UpdateContentStats`(IN p_content_id VARCHAR(24),
    IN p_content_type VARCHAR(50),
    IN p_action_type VARCHAR(50),
    IN p_increment INT)
BEGIN
    INSERT INTO content_stats (content_id, content_type, view_count, like_count, favorite_count, comment_count, share_count)
    VALUES (p_content_id, p_content_type, 
            CASE WHEN p_action_type = 'view' THEN p_increment ELSE 0 END,
            CASE WHEN p_action_type = 'like' THEN p_increment ELSE 0 END,
            CASE WHEN p_action_type = 'favorite' THEN p_increment ELSE 0 END,
            CASE WHEN p_action_type = 'comment' THEN p_increment ELSE 0 END,
            CASE WHEN p_action_type = 'share' THEN p_increment ELSE 0 END)
    ON DUPLICATE KEY UPDATE
        view_count = view_count + CASE WHEN p_action_type = 'view' THEN p_increment ELSE 0 END,
        like_count = like_count + CASE WHEN p_action_type = 'like' THEN p_increment ELSE 0 END,
        favorite_count = favorite_count + CASE WHEN p_action_type = 'favorite' THEN p_increment ELSE 0 END,
        comment_count = comment_count + CASE WHEN p_action_type = 'comment' THEN p_increment ELSE 0 END,
        share_count = share_count + CASE WHEN p_action_type = 'share' THEN p_increment ELSE 0 END;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table users
-- ----------------------------
DROP TRIGGER IF EXISTS `after_user_insert`;
delimiter ;;
CREATE TRIGGER `after_user_insert` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    INSERT INTO user_stats (user_id) VALUES (NEW.id);
    INSERT INTO user_roles (user_id, role_name) VALUES (NEW.id, 'user');
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
