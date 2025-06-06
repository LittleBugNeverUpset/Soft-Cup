package com.littlebug.utils;

import org.apache.ibatis.annotations.Param;

/**
 * 统一返回结果状态信息类
 *
 */
public enum ResultCodeEnum {

    SUCCESS(200,"success"),
    STUDENT_NUMBER_NOT_EXIST(501,"studentNumberNotExist"),
    PASSWORD_ERROR(503,"passwordError"),
    NOTLOGIN(504,"notLogin"),
    STUDENTNUMBER_EXIST(505,"StudentNumberExisted"),
    PARAM_ERROR(505,"Invilid Param");
    private Integer code;
    private String message;
    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
