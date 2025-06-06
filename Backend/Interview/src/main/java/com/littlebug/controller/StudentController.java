package com.littlebug.controller;


import com.littlebug.pojo.Student;
import com.littlebug.service.StudentService;
import com.littlebug.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/student")

public class StudentController {
    @Autowired
    private StudentService studentService;

//    检查学号是否已经存在
    @PostMapping ("checkStudentNumber")
    public Result checkStudentNumber(String StNo){
        Result result = studentService.checkStudentNo(StNo);
        return result;
    }
//    注册功能

    @PostMapping("regist")
    public Result regsit(@RequestBody Student student){
        Result result = studentService.regist(student);
        return  result;


    }

//    登录功能
    @PostMapping("login")
    public Result login(@RequestBody Student student){
        Result result = studentService.login(student);
        System.out.println("result = " + result);
        return result;
    }
//    获取学生信息
    @GetMapping("StudentInfo")
    public Result getStudentInfo(@RequestHeader String token){
        Result result = studentService.getStudentInfo(token);
        return result;
    }



}
