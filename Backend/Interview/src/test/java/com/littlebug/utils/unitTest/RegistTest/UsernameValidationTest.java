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
public class UsernameValidationTest {

    @Autowired
    private UserService userService;

    // 空值测试（UT-01）
    @Test
    void whenUsernameNull_thenReturnEmptyError() {
        Result<?> result = registerUser(null);

        assertEquals(ResultCodeEnum.USERNAME_ERROR.getCode(), result.getCode());
        assertEquals("Username Empty", result.getData());
    }

    // 空白测试（UT-02）
    @Test
    void whenUsernameBlank_thenReturnEmptyError() {
        Result<?> result = registerUser("   ");
        assertEquals(ResultCodeEnum.USERNAME_ERROR.getCode(), result.getCode());
        assertEquals("Username Empty", result.getData());
    }

    // 长度测试（UT-03）
    @Test
    void whenUsernameTooShort_thenReturnLengthError() {
        Result<?> result = registerUser("a");
        assertEquals(ResultCodeEnum.USERNAME_ERROR.getCode(), result.getCode());
        assertEquals("Username Too Short", result.getData());
    }

    // 数字测试（UT-04）
    @Test
    void whenUsernameContainsDigit_thenReturnDigitError() {
        Result<?> result = registerUser("user1");
        assertEquals(ResultCodeEnum.USERNAME_ERROR.getCode(), result.getCode());
        assertEquals("Username Contains Digit", result.getData());
    }

    // 成功测试（UT-05）
    @Test
    void whenUsernameValid_thenPassValidation() {
        User user = new User();
        user.setUsername("valid");
        user.setPasswordHash("ValidPass1$"); // 其他字段需合法
        user.setEmail("12312313");
        
        Result<?> result = userService.regist(user);
        assertEquals(Result.ok(null).getCode(), result.getCode());
        assertEquals("success", result.getMessage());
    }

    // 辅助方法
    private Result<?> registerUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("ValidPass1$"); // 确保其他字段合法
        user.setEmail("12345678");
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