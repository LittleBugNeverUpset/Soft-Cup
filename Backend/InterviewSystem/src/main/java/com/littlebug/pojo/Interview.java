package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName interview
 */
@TableName(value ="interview")
@Data
public class Interview {
    private Long id;

    private Long studentId;

    private String positionType;

    private String positionName;

    private Date startTime;

    private Date endTime;

    private Integer totalDuration;

    private Integer status;

    private Integer currentQuestionIndex;

    private String videoUrl;

    private Date createTime;

    private Date updateTime;
}