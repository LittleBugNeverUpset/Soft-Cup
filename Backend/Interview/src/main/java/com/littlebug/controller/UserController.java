package com.littlebug.controller;

import com.littlebug.pojo.User;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/student")
public class UserController {
    @Autowired
    private UserService userService;
    //    检查学号是否已经存在
    @PostMapping("checkStudentNumber")
    public Result checkStudentNumber(String userName){
        Result result = userService.checkUserName(userName);
        return result;
    }
    //    注册功能
    @PostMapping("regist")
    public Result regist(@RequestBody User user){
        Result result = userService.regist(user);
        return result;
    }

    //    登录功能
    @PostMapping("login")
    public Result login(@RequestBody User user){
        Result result = userService.login(user);
        System.out.println("result = " + result);
        return result;
    }
    //    获取学生信息
    @GetMapping("getUserInfo")
    public Result userInfo(@RequestHeader String token){
        Result result = userService.getUserInfo(token);
        return result;
    }


}
