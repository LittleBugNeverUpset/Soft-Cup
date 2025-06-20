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

    private Long userId;

    private String positionType;

    private Object status;

    private Integer currentQuestionSeq;

    private Date createdAt;

    private Date updatedAt;
}