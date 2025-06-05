package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName interview_question
 */
@TableName(value ="interview_question")
@Data
public class InterviewQuestion {
    private Long id;

    private Long interviewId;

    private Integer questionIndex;

    private String questionText;

    private Integer questionType;

    private Integer difficulty;

    private Integer answerDuration;

    private Date createTime;
}