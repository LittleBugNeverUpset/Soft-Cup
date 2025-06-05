package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName multimodal_data
 */
@TableName(value ="multimodal_data")
@Data
public class MultimodalData {
    private Long id;

    private Long interviewId;

    private String dataType;

    private String dataUrl;

    private String featuresJson;

    private Date createTime;
}