package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName interview_state
 */
@TableName(value ="interview_state")
@Data
public class InterviewState {
    private Long id;

    private Long interviewId;

    private String state;

    private Date createdAt;
}