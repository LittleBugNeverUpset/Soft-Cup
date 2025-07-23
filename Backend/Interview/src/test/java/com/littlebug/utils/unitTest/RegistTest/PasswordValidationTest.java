package com.littlebug.utils.unitTest.RegistTest;


import com.littlebug.pojo.User;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class PasswordValidationTest {

    @Autowired
    private UserService userService;

    // 空值测试（UT-01）
    @Test
    void whenPasswordNull_thenReturnTooShortError() {
        Result<?> result = registerUserWithPassword(null);
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password Too Short", result.getData());
    }

    // 长度测试（UT-02）
    @Test
    void whenPasswordTooShort_thenReturnTooShortError() {
        Result<?> result = registerUserWithPassword("123");
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password Too Short", result.getData());
    }

    // 纯数字测试（UT-03）
    @Test
    void whenPasswordOnlyDigits_thenReturnWeakError() {
        Result<?> result = registerUserWithPassword("12345678");
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password wake", result.getData());
    }

    // 纯字母测试（UT-04）
    @Test
    void whenPasswordOnlyLetters_thenReturnWeakError() {
        Result<?> result = registerUserWithPassword("abcdefgh");
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password wake", result.getData());
    }
    // 特殊字符测试
    @Test
    void whenPasswordNoLetterButSpecialChar_thenPass() {
        Result<?> result = registerUserWithPassword("@#@#@#$#%^");
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password wake", result.getData());
    }



    // 成功(大小写混合)测试（UT-05）
    @Test
    void whenPasswordValid_thenPassValidation() {
        Result<?> result = registerUserWithPassword("Abc123$#");
        assertEquals(Result.ok(null).getCode(), result.getCode());
        assertEquals("success", result.getMessage());
    }

    // 辅助方法
    private Result<?> registerUserWithPassword(String password) {
        User user = new User();
        user.setUsername("testuser"); // 确保用户名合法
        user.setPasswordHash(password);
        user.setEmail("12345678"); // 确保学号合法
        return userService.regist(user);
    }

    private void assertErrorResult(Result<?> result, 
                                ResultCodeEnum code, 
                                String message) {
        assertEquals(code.getCode(), result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }
}