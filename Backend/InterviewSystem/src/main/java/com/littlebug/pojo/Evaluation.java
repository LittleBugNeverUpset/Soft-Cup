package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName evaluation
 */
@TableName(value ="evaluation")
@Data
public class Evaluation {
    private Long id;

    private Long interviewId;

    private BigDecimal contentScore;

    private BigDecimal expressionScore;

    private BigDecimal professionalScore;

    private BigDecimal comprehensiveScore;

    private String feedback;

    private String audioFeedbackUrl;

    private Integer evaluatorType;

    private Date createTime;

    private Date updateTime;

    private Integer evaluationMode;

    private String evaluationVersion;
}