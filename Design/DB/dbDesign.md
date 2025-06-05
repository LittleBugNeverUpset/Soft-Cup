# 多模态面试评测系统数据字典

## 1. 学生表(student)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 学生ID |
| student_no | varchar(32) | NO | UK | - | 学号(加密) |
| name | varchar(64) | NO |  | - | 姓名 |
| encrypted_pwd | varchar(128) | NO |  | - | BCrypt加密密码 |
| college | varchar(64) | YES |  | NULL | 学院 |
| major | varchar(64) | YES |  | NULL | 专业 |
| grade | varchar(16) | YES |  | NULL | 年级 |
| status | tinyint | NO |  | 1 | 账号状态(0-禁用,1-正常) |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NO |  | CURRENT_TIMESTAMP | 更新时间 |

## 2. 面试记录表(interview)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 面试ID |
| student_id | bigint | NO | FK | - | 学生ID |
| position_type | varchar(32) | NO |  | - | 岗位类型 |
| status | tinyint | NO |  | 0 | 状态(0-准备中,1-进行中,2-评估中,3-已完成) |
| start_time | datetime | YES |  | NULL | 开始时间 |
| end_time | datetime | YES |  | NULL | 结束时间 |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |

## 3. 能力维度表(ability_dimension)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | int | NO | PK | AUTO_INCREMENT | 维度ID |
| code | varchar(32) | NO | UK | - | 维度编码 |
| name | varchar(64) | NO |  | - | 维度名称 |
| weight | decimal(5,2) | YES |  | 0.00 | 权重 |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |

## 4. 评估结果表(evaluation)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 评估ID |
| interview_id | bigint | NO | FK | - | 面试ID |
| evaluation_mode | tinyint | NO |  | 0 | 评估模式(0-自动,1-人工) |
| evaluation_version | varchar(32) | YES |  | 1.0 | 模型版本 |
| feedback | text | YES |  | NULL | 结构化反馈(JSON) |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |

## 5. 维度评分表(dimension_score)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 评分ID |
| evaluation_id | bigint | NO | FK | - | 评估ID |
| dimension_code | varchar(32) | NO |  | - | 维度编码 |
| score | decimal(5,2) | NO |  | 0.00 | 维度得分 |
| sub_scores_json | text | YES |  | NULL | 子维度得分(JSON) |

## 6. 多模态数据表(multimodal_data)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 数据ID |
| interview_id | bigint | NO | FK | - | 面试ID |
| data_type | varchar(32) | NO |  | - | 数据类型 |
| features_json | text | YES |  | NULL | 特征数据(JSON) |
| create_time | datetime | NO |  | CURRENT_TIMESTAMP | 创建时间 |

## 7. 面试状态表(interview_state)

| 字段名 | 类型 | 允许空 | 键 | 默认值 | 说明 |
|--------|------|--------|----|--------|------|
| id | bigint | NO | PK | AUTO_INCREMENT | 状态ID |
| interview_id | bigint | NO | FK | - | 面试ID |
| state | varchar(20) | NO |  | - | 状态值 |
| transition_time | datetime | NO |  | CURRENT_TIMESTAMP | 切换时间 |

## 枚举值说明

### 面试状态枚举
| 值 | 说明 |
|----|------|
| 0 | 准备中 |
| 1 | 进行中 |
| 2 | 评估中 |
| 3 | 已完成 |

### 数据类型枚举
| 值 | 说明 |
|----|------|
| AUDIO | 语音数据 |
| VIDEO | 视频数据 |
| TEXT | 文本数据 |
| RESUME | 简历数据 |

### 核心能力维度
| 维度编码 | 说明 |
|----------|------|
| PROFESSIONAL | 专业知识 |
| SKILL_MATCH | 技能匹配 |
| LANGUAGE | 语言表达 |
| LOGIC | 逻辑思维 |
| INNOVATION | 创新能力 |
| STRESS | 应变能力 |