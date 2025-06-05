package com.littlebug.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName student
 */
@TableName(value ="student")
@Data
public class Student {
    private Long id;

    private String studentNo;

    private String name;

    private String encryptedPwd;

    private String college;

    private String major;

    private String grade;

    private String phone;

    private String email;

    private String avatarUrl;

    private Integer status;

    private Date lastLoginTime;

    private Date createTime;

    private Date updateTime;
}