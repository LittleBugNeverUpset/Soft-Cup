package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @TableName dimension_score
 */
@TableName(value ="dimension_score")
@Data
public class DimensionScore {
    private Long id;

    private Long evaluationId;

    private String dimensionCode;

    private BigDecimal score;

    private String subScoresJson;

    private String feedback;
}