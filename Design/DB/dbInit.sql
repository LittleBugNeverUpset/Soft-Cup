CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE interview_system;
-- 学生表
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_no` varchar(32) NOT NULL COMMENT '学号',
  `name` varchar(64) NOT NULL COMMENT '姓名',
  `encrypted_pwd` varchar(128) NOT NULL COMMENT '加密密码',
  `college` varchar(64) DEFAULT NULL COMMENT '学院',
  `major` varchar(64) DEFAULT NULL COMMENT '专业',
  `grade` varchar(16) DEFAULT NULL COMMENT '年级',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0-禁用,1-正常)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  KEY `idx_college_major` (`college`,`major`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生信息表';

-- 面试记录表
CREATE TABLE `interview` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '面试ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `position_type` varchar(32) NOT NULL COMMENT '岗位类型',
  `position_name` varchar(64) NOT NULL COMMENT '岗位名称',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `total_duration` int DEFAULT '0' COMMENT '总时长(秒)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0-准备中,1-进行中,2-评估中,3-已完成,4-已取消)',
  `current_question_index` int DEFAULT '0' COMMENT '当前问题序号',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_position_type` (`position_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录表';

-- 面试状态记录表
CREATE TABLE `interview_state` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '状态记录ID',
  `interview_id` bigint NOT NULL COMMENT '面试ID',
  `from_state` tinyint DEFAULT NULL COMMENT '原状态',
  `to_state` tinyint NOT NULL COMMENT '新状态',
  `transition_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态切换时间',
  `operator` varchar(32) DEFAULT NULL COMMENT '操作人(system/student)',
  `context_json` text COMMENT '状态上下文(JSON)',
  PRIMARY KEY (`id`),
  KEY `idx_interview_id` (`interview_id`),
  KEY `idx_transition_time` (`transition_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试状态记录表';

-- 评估结果表
CREATE TABLE `evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  `interview_id` bigint NOT NULL COMMENT '面试ID',
  `content_score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '内容得分',
  `expression_score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '表达得分',
  `professional_score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '专业得分',
  `comprehensive_score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '综合得分',
  `feedback` text COMMENT '文字反馈',
  `audio_feedback_url` varchar(255) DEFAULT NULL COMMENT '语音反馈URL',
  `evaluator_type` tinyint NOT NULL DEFAULT '0' COMMENT '评估类型(0-AI,1-人工)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_interview_id` (`interview_id`),
  KEY `idx_scores` (`content_score`,`expression_score`,`professional_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评估结果表';

-- 面试问题表
CREATE TABLE `interview_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `interview_id` bigint NOT NULL COMMENT '面试ID',
  `question_index` int NOT NULL COMMENT '问题序号',
  `question_text` text NOT NULL COMMENT '问题内容',
  `question_type` tinyint NOT NULL COMMENT '问题类型(0-技术,1-行为,2-情景)',
  `difficulty` tinyint DEFAULT '2' COMMENT '难度(1-5)',
  `answer_duration` int DEFAULT NULL COMMENT '建议回答时长(秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_interview_id` (`interview_id`,`question_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试问题表';

-- 外键约束（生产环境建议根据实际情况添加）
ALTER TABLE `interview` ADD CONSTRAINT `fk_interview_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);
ALTER TABLE `interview_state` ADD CONSTRAINT `fk_state_interview` FOREIGN KEY (`interview_id`) REFERENCES `interview` (`id`);
ALTER TABLE `evaluation` ADD CONSTRAINT `fk_evaluation_interview` FOREIGN KEY (`interview_id`) REFERENCES `interview` (`id`);
ALTER TABLE `interview_question` ADD CONSTRAINT `fk_question_interview` FOREIGN KEY (`interview_id`) REFERENCES `interview` (`id`);

-- 初始化数据
INSERT INTO `student` (`student_no`, `name`, `encrypted_pwd`, `college`, `major`, `grade`, `phone`, `email`) 
VALUES 
('20230001', '张三', '$2a$10$xJwL5v5zLz3hBqNQYbZJX.9vRZ1Qd0yJW8Jk6f3qLm9VbW1s2XyYz', '计算机学院', '计算机科学与技术', '大三', '13800138001', 'zhangsan@example.com'),
('20230002', '李四', '$2a$10$xJwL5v5zLz3hBqNQYbZJX.9vRZ1Qd0yJW8Jk6f3qLm9VbW1s2XyYz', '经济学院', '金融学', '大四', '13800138002', 'lisi@example.com');


-- 新增能力维度表
CREATE TABLE `ability_dimension` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL COMMENT '维度编码',
  `name` varchar(64) NOT NULL COMMENT '维度名称',
  `description` varchar(255) DEFAULT NULL COMMENT '维度说明',
  `weight` decimal(5,2) DEFAULT '0.00' COMMENT '权重(0-1)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能力维度表';

-- 初始化核心能力维度
INSERT INTO `ability_dimension` (`code`, `name`, `description`, `weight`) VALUES
('PROFESSIONAL', '专业知识水平', '评估候选人的专业知识和理论掌握程度', 0.25),
('SKILL_MATCH', '技能匹配度', '评估技能与岗位要求的匹配程度', 0.20),
('LANGUAGE', '语言表达能力', '评估语言组织、表达流畅度和感染力', 0.20),
('LOGIC', '逻辑思维能力', '评估问题分析的逻辑性和条理性', 0.15),
('INNOVATION', '创新能力', '评估创新思维和解决方案的新颖性', 0.10),
('STRESS', '应变抗压能力', '评估压力情境下的应对表现', 0.10);

-- 重构评估结果表
ALTER TABLE `evaluation` 
ADD COLUMN `evaluation_mode` tinyint NOT NULL DEFAULT '0' COMMENT '评估模式(0-自动,1-人工修正)',
ADD COLUMN `evaluation_version` varchar(32) DEFAULT '1.0' COMMENT '评估模型版本',
MODIFY COLUMN `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '结构化反馈(JSON格式)';

-- 创建多模态原始数据表
CREATE TABLE `multimodal_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_id` bigint NOT NULL,
  `data_type` varchar(32) NOT NULL COMMENT '数据类型(AUDIO/VIDEO/TEXT/RESUME)',
  `data_url` varchar(255) DEFAULT NULL COMMENT '原始数据URL',
  `features_json` text COMMENT '特征提取结果(JSON)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_interview_id` (`interview_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='多模态原始数据表';

-- 创建维度评分表
CREATE TABLE `dimension_score` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `evaluation_id` bigint NOT NULL,
  `dimension_code` varchar(32) NOT NULL,
  `score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '维度得分',
  `sub_scores_json` text COMMENT '子维度得分(JSON)',
  `feedback` text COMMENT '维度专属反馈',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eval_dimension` (`evaluation_id`,`dimension_code`),
  CONSTRAINT `fk_dimension_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维度评分表';