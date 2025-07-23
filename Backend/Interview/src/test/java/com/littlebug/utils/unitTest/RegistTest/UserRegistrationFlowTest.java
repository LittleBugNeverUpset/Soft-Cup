package com.littlebug.utils.unitTest.RegistTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.littlebug.mapper.UserMapper;
import com.littlebug.pojo.User;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class UserRegistrationFlowTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    // 测试工具方法
    private Result<?> registerUser(String username, String password, String studentId) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(studentId); // 假设学号存储在email字段
        return userService.regist(user);
    }

    // UT-01: 用户名无效
    @Test
    void whenInvalidUsername_thenReturnUsernameError() {
        Result<?> result = registerUser("1user", "ValidPass1$", "12345678");
        assertEquals(ResultCodeEnum.USERNAME_ERROR.getCode(), result.getCode());
        assertEquals("user name Error", result.getMessage());
    }

    // UT-02: 密码无效
    @Test
    void whenInvalidPassword_thenReturnPasswordError() {
        Result<?> result = registerUser("validuser", "weak", "12345678");
        assertEquals(ResultCodeEnum.PASSWORD_UNQUALIFY.getCode(), result.getCode());
        assertEquals("Password_Unqualify", result.getMessage());
    }

    // UT-03: 学号无效
    @Test
    void whenInvalidStudentId_thenReturnStudentIdError() {
        Result<?> result = registerUser("validuser", "ValidPass1$", "01234567");
        assertEquals(ResultCodeEnum.STUDEN_ID_UNQUALIFY.getCode(), result.getCode());
        assertEquals("StudenId Unqualify", result.getMessage());
    }

    // UT-04: 全部有效
    @Test
    void whenAllValid_thenReturnSuccess() {
        String username = "ChongHy";
        Result<?> result = registerUser(username, "ValidPass1$", "12345678");
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        
        // 验证数据库写入
        assertNotNull(userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)));
    }
}