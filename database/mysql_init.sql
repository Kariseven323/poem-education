-- {{RIPER-5+SMART-6:
--   Action: "Parallel-Added"
--   Task_ID: "mysql-database-init"
--   Timestamp: "2025-08-07T09:56:16+08:00"
--   Authoring_Subagent: "data-architect"
--   Principle_Applied: "数据库设计最佳实践"
--   Quality_Check: "基于现有MongoDB数据结构的MySQL补充设计"
-- }}

-- ===========================================
-- 诗词交流鉴赏平台 MySQL数据库初始化脚本
-- 基于现有MongoDB数据结构设计
-- ===========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS poem_education 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE poem_education;

-- ===========================================
-- 用户管理相关表
-- ===========================================

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) COMMENT '用户基础信息表';

-- 用户角色表
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名：admin/user/vip',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_name)
) COMMENT '用户角色关联表';

-- ===========================================
-- 用户行为相关表
-- ===========================================

-- 用户行为表（点赞、收藏、浏览等）
CREATE TABLE user_actions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(24) NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
    target_type VARCHAR(50) NOT NULL COMMENT '目标类型：guwen/creation/comment/sentence',
    action_type VARCHAR(50) NOT NULL COMMENT '行为类型：like/favorite/view/share',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_target_action (user_id, target_id, target_type, action_type),
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_id, target_type),
    INDEX idx_action_type (action_type),
    INDEX idx_created_at (created_at)
) COMMENT '用户行为记录表';

-- 用户收藏表
CREATE TABLE user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(24) NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
    target_type VARCHAR(50) NOT NULL COMMENT '收藏类型：guwen/sentence/writer/creation',
    folder_name VARCHAR(100) DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
    notes TEXT COMMENT '收藏备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_favorite (user_id, target_id, target_type),
    INDEX idx_user_folder (user_id, folder_name),
    INDEX idx_target_type (target_type)
) COMMENT '用户收藏表';

-- 学习记录表
CREATE TABLE learning_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id VARCHAR(24) NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
    target_type VARCHAR(50) NOT NULL COMMENT '学习内容类型：guwen/sentence',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    progress_status TINYINT DEFAULT 0 COMMENT '进度状态：0未开始 1学习中 2已完成',
    last_position TEXT COMMENT '学习位置记录(JSON)',
    study_notes TEXT COMMENT '学习笔记',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_target (user_id, target_id, target_type),
    INDEX idx_user_progress (user_id, progress_status),
    INDEX idx_updated_at (updated_at)
) COMMENT '用户学习记录表';

-- ===========================================
-- 统计分析相关表
-- ===========================================

-- 内容统计表（对应MongoDB文档的统计信息）
CREATE TABLE content_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id VARCHAR(24) NOT NULL COMMENT 'MongoDB ObjectId字符串(24字符)',
    content_type VARCHAR(50) NOT NULL COMMENT '内容类型：guwen/sentence/writer/creation',
    view_count BIGINT DEFAULT 0 COMMENT '浏览次数',
    like_count BIGINT DEFAULT 0 COMMENT '点赞次数',
    favorite_count BIGINT DEFAULT 0 COMMENT '收藏次数',
    comment_count BIGINT DEFAULT 0 COMMENT '评论次数',
    share_count BIGINT DEFAULT 0 COMMENT '分享次数',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content (content_id, content_type),
    INDEX idx_content_type (content_type),
    INDEX idx_view_count (view_count),
    INDEX idx_like_count (like_count)
) COMMENT '内容统计表';

-- 用户统计表
CREATE TABLE user_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_views BIGINT DEFAULT 0 COMMENT '总浏览数',
    total_likes BIGINT DEFAULT 0 COMMENT '总点赞数',
    total_favorites BIGINT DEFAULT 0 COMMENT '总收藏数',
    total_comments BIGINT DEFAULT 0 COMMENT '总评论数',
    total_creations BIGINT DEFAULT 0 COMMENT '总创作数',
    study_days INT DEFAULT 0 COMMENT '学习天数',
    last_active_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_stats (user_id)
) COMMENT '用户统计表';

-- ===========================================
-- 系统配置相关表
-- ===========================================

-- 系统配置表
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
    description VARCHAR(255) COMMENT '配置描述',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开：1公开 0私有',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_is_public (is_public)
) COMMENT '系统配置表';

-- ===========================================
-- 初始化数据
-- ===========================================

-- 插入默认角色
INSERT INTO system_configs (config_key, config_value, config_type, description, is_public) VALUES
('default_user_role', 'user', 'string', '默认用户角色', 0),
('max_daily_creations', '10', 'number', '每日最大创作数量', 0),
('enable_ai_scoring', 'true', 'boolean', '是否启用AI评分', 0),
('hot_content_refresh_interval', '3600', 'number', '热门内容刷新间隔(秒)', 0),
('max_comment_length', '1000', 'number', '评论最大长度', 1),
('enable_user_registration', 'true', 'boolean', '是否允许用户注册', 1),
('data_sync_interval', '300', 'number', '数据同步间隔(秒)', 0),
('enable_data_consistency_check', 'true', 'boolean', '是否启用数据一致性检查', 0),
('cache_ttl_seconds', '1800', 'number', '缓存过期时间(秒)', 0);

-- 创建管理员用户（密码：admin123，需要在应用中重新设置）
INSERT INTO users (username, email, password_hash, nickname, status) VALUES
('admin', 'admin@poem-education.com', '$2a$10$placeholder', '系统管理员', 1);

-- 为管理员分配角色
INSERT INTO user_roles (user_id, role_name) VALUES
(1, 'admin'),
(1, 'user');

-- ===========================================
-- 创建视图（便于查询）
-- ===========================================

-- 用户详细信息视图
CREATE VIEW user_details AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.nickname,
    u.avatar,
    u.bio,
    u.status,
    u.created_at,
    GROUP_CONCAT(ur.role_name) as roles,
    us.total_views,
    us.total_likes,
    us.total_favorites,
    us.total_comments,
    us.total_creations,
    us.study_days,
    us.last_active_at
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN user_stats us ON u.id = us.user_id
GROUP BY u.id;

-- 热门内容视图
CREATE VIEW hot_contents AS
SELECT 
    content_id,
    content_type,
    view_count,
    like_count,
    favorite_count,
    comment_count,
    (view_count * 1 + like_count * 3 + favorite_count * 5 + comment_count * 2) as hot_score
FROM content_stats
WHERE view_count > 0
ORDER BY hot_score DESC;

-- ===========================================
-- 创建存储过程
-- ===========================================

DELIMITER //

-- 更新内容统计的存储过程
CREATE PROCEDURE UpdateContentStats(
    IN p_content_id VARCHAR(24),
    IN p_content_type VARCHAR(50),
    IN p_action_type VARCHAR(50),
    IN p_increment INT
)
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
END //

DELIMITER ;

-- ===========================================
-- 创建触发器
-- ===========================================

DELIMITER //

-- 用户注册时自动创建统计记录
CREATE TRIGGER after_user_insert
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_stats (user_id) VALUES (NEW.id);
    INSERT INTO user_roles (user_id, role_name) VALUES (NEW.id, 'user');
END //

DELIMITER ;
