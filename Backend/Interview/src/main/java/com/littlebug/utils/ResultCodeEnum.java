package com.littlebug.utils;

import org.apache.ibatis.annotations.Param;

/**
 * 统一返回结果状态信息类
 *
 */
public enum ResultCodeEnum {

    SUCCESS(200,"success"),
    USERNAME_EXIST(501,"user name existed"),
    USERNAME_ERROR(502,"user name not existed"),
    PASSWORD_ERROR(503,"user password error" ),
    NOTLOGIN(504, "not log in"),
    USERNAME_USED(505, "user name used"),
    PROCESS_ERROR(506, "process error");
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
