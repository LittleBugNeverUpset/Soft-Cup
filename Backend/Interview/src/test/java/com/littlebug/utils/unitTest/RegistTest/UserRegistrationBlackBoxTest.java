package com.littlebug.utils.unitTest.RegistTest;

import com.littlebug.pojo.User;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserRegistrationBlackBoxTest {

    @Autowired
    private UserService userService;

    // 创建测试用户
    private User createTestUser(String username, String password, String studentId) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(studentId);
        return user;
    }

    // 新断言方法（验证data字段）
    private void assertErrorData(Result<?> result, String expectedData) {
        assertEquals(expectedData, result.getData());
    }
    @Nested
    @DisplayName("用户名测试")
    class UsernameValidationTest {

        @Test
        @DisplayName("NULL用户名应返回'Username Empty'")
        void nullUsername_shouldFail() {
            Result<?> result = userService.regist(createTestUser(null, "ValidPass1$", "12345678"));
            assertErrorData(result, "Username Empty");
        }

        @Test
        @DisplayName("空用户名应返回'Username Empty'")
        void emptyUsername_shouldFail() {
            Result<?> result = userService.regist(createTestUser("", "ValidPass1$", "12345678"));
            assertErrorData(result, "Username Empty");
        }

        @Test
        @DisplayName("过短用户名应返回'Username Too Short'")
        void shortUsername_shouldFail() {
            Result<?> result = userService.regist(createTestUser("a", "ValidPass1$", "12345678"));
            assertErrorData(result, "Username Too Short");
        }

        @Test
        @DisplayName("含数字用户名应返回'Username Contains Digit'")
        void usernameWithDigit_shouldFail() {
            Result<?> result = userService.regist(createTestUser("user1", "ValidPass1$", "12345678"));
            assertErrorData(result, "Username Contains Digit");
        }
    }
    @Nested
    @DisplayName("密码测试")
    class PasswordValidationTest {

        @Test
        @DisplayName("NULL密码应返回'Password Too Short'")
        void nullPassword_shouldFail() {
            Result<?> result = userService.regist(createTestUser("testUser", null, "12345678"));
            assertErrorData(result, "Password Too Short");
        }

        @Test
        @DisplayName("过短密码应返回'Password Too Short'")
        void shortPassword_shouldFail() {
            Result<?> result = userService.regist(createTestUser("testUser", "123", "12345678"));
            assertErrorData(result, "Password Too Short");
        }

        @Test
        @DisplayName("弱密码应返回'Password Weak'")
        void weakPassword_shouldFail() {
            Result<?> result = userService.regist(createTestUser("testUser", "abcdefgh", "12345678"));
            assertErrorData(result, "Password Weak");
        }
    }
    @Nested
    @DisplayName("学号测试")
    class StudentIdValidationTest {

        @Test
        @DisplayName("NULL学号应返回'Invalid Student Id'")
        void nullStudentId_shouldFail() {
            Result<?> result = userService.regist(createTestUser("testUser", "ValidPass1$", null));
            assertErrorData(result, "Invalid Student Id");
        }

        @Test
        @DisplayName("无效学号应返回'Invalid Student Id'")
        void invalidStudentId_shouldFail() {
            Result<?> result = userService.regist(createTestUser("testUser", "ValidPass1$", "01234567"));
            assertErrorData(result, "Invalid Student Id");
        }
    }
    @Nested
    @DisplayName("成功场景测试")
    class SuccessCasesTest {

        @Test
        @DisplayName("有效数据应返回success")
        void validData_shouldReturnSuccess() {
            Result<?> result = userService.regist(
                    createTestUser("validUser", "ValidPass1$", "12345678")
            );
            assertEquals("success", result.getMessage());
        }
    }
}
