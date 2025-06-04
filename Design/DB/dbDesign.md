# 面试系统数据库字典

## 1. 学生表(student)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 学生ID |
| student_no | varchar(32) | NO | UK | - | 学号 |
| name | varchar(64) | NO |  | - | 姓名 |
| encrypted_pwd | varchar(128) | NO |  | - | BCrypt加密密码 |
| college | varchar(64) | YES |  | NULL | 学院 |
| major | varchar(64) | YES |  | NULL | 专业 |
| grade | varchar(16) | YES |  | NULL | 年级 |
| phone | varchar(20) | YES |  | NULL | 手机号 |
| email | varchar(128) | YES |  | NULL | 邮箱 |
| avatar_url | varchar(255) | YES |  | NULL | 头像URL |
| status | tinyint | NO |  | 1 | 账号状态(0-禁用,1-正常) |
| last_login_time | datetime | YES |  | NULL | 最后登录时间 |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NO |  | CURRENT_TIMESTAMP | 更新时间 |

## 2. 面试记录表(interview)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 面试ID |
| student_id | bigint | NO | FK | - | 关联学生ID |
| position_type | varchar(32) | NO |  | - | 岗位类型(DEV/PM等) |
| position_name | varchar(64) | NO |  | - | 岗位名称 |
| start_time | datetime | YES |  | NULL | 开始时间 |
| end_time | datetime | YES |  | NULL | 结束时间 |
| total_duration | int | YES |  | 0 | 总时长(秒) |
| status | tinyint | NO |  | 0 | 状态(0-准备中,1-进行中,2-评估中,3-已完成,4-已取消) |
| current_question_index | int | YES |  | 0 | 当前问题序号 |
| video_url | varchar(255) | YES |  | NULL | 视频存储URL |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NO |  | CURRENT_TIMESTAMP | 更新时间 |

## 3. 面试状态记录表(interview_state)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 状态记录ID |
| interview_id | bigint | NO | FK | - | 关联面试ID |
| from_state | tinyint | YES |  | NULL | 原状态 |
| to_state | tinyint | NO |  | - | 新状态 |
| transition_time | datetime | NO |  | CURRENT_TIMESTAMP | 状态切换时间 |
| operator | varchar(32) | YES |  | NULL | 操作人 |
| context_json | text | YES |  | NULL | 状态上下文(JSON) |

## 4. 评估结果表(evaluation)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 评估ID |
| interview_id | bigint | NO | FK | - | 关联面试ID |
| content_score | decimal(5,2) | NO |  | 0.00 | 内容得分(0-100) |
| expression_score | decimal(5,2) | NO |  | 0.00 | 表达得分(0-100) |
| professional_score | decimal(5,2) | NO |  | 0.00 | 专业得分(0-100) |
| comprehensive_score | decimal(5,2) | NO |  | 0.00 | 综合得分(0-100) |
| feedback | text | YES |  | NULL | 文字反馈 |
| audio_feedback_url | varchar(255) | YES |  | NULL | 语音反馈URL |
| evaluator_type | tinyint | NO |  | 0 | 评估类型(0-AI,1-人工) |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NO |  | CURRENT_TIMESTAMP | 更新时间 |

## 5. 面试问题表(interview_question)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 问题ID |
| interview_id | bigint | NO | FK | - | 关联面试ID |
| question_index | int | NO |  | - | 问题序号 |
| question_text | text | NO |  | - | 问题内容 |
| question_type | tinyint | NO |  | - | 问题类型(0-技术,1-行为,2-情景) |
| difficulty | tinyint | YES |  | 2 | 难度(1-5) |
| answer_duration | int | YES |  | NULL | 建议回答时长(秒) |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |