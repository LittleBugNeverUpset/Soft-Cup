package com.littlebug.controller;


import com.littlebug.pojo.Student;
import com.littlebug.service.StudentService;
import com.littlebug.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")

public class UserController {
    @Autowired
    private StudentService studentService;

//    登录功能
    @PostMapping("login")
    public Result login(@RequestBody Student student){
        Result result = studentService.login(student);
        System.out.println("result = " + result);
        return result;
    }


}
