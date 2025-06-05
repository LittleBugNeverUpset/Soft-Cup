package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName ability_dimension
 */
@TableName(value ="ability_dimension")
@Data
public class AbilityDimension {
    private Integer id;

    private String code;

    private String name;

    private String description;

    private BigDecimal weight;

    private Date createTime;
}