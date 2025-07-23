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
public class StudentIdValidationTest {

    @Autowired
    private UserService userService;

    // 空值测试（UT-01）
    @Test
    void whenStudentIdNull_thenReturnInvalidError() {
        Result<?> result = registerUserWithStudentId(null);
        assertEquals(ResultCodeEnum.STUDEN_ID_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Invalid Student Id", result.getData());
    }

    // 首位为0测试（UT-02）
    @Test
    void whenStudentIdStartsWithZero_thenReturnInvalidError() {
        Result<?> result = registerUserWithStudentId("01234567");
        assertEquals(ResultCodeEnum.STUDEN_ID_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Invalid Student Id", result.getData());
    }

    // 长度不足测试（UT-03）
    @Test
    void whenStudentIdTooShort_thenReturnInvalidError() {
        Result<?> result = registerUserWithStudentId("1234567");
        assertEquals(ResultCodeEnum.STUDEN_ID_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Invalid Student Id", result.getData());
    }

    // 含字母测试（UT-04）
    @Test
    void whenStudentIdContainsLetter_thenReturnInvalidError() {
        Result<?> result = registerUserWithStudentId("A1234567");
        assertEquals(ResultCodeEnum.STUDEN_ID_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Invalid Student Id", result.getData());
    }

    // 成功测试（UT-05）
    @Test
    void whenStudentIdValid_thenPassValidation() {
        Result<?> result = registerUserWithStudentId("28225156");
        assertEquals(Result.ok(null).getCode(), result.getCode());
        assertEquals("success", result.getMessage());
    }

    // 辅助方法
    private Result<?> registerUserWithStudentId(String studentId) {
        User user = new User();
        user.setUsername("validuser"); // 确保用户名合法
        user.setPasswordHash("ValidPass1$"); // 确保密码合法
        user.setEmail(studentId); // 学号存储在email字段
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