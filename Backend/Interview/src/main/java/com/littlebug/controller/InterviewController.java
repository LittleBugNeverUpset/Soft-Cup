package com.littlebug.controller;

import com.littlebug.service.InterviewService;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 面试控制器示例
@RestController
@RequestMapping("/api/interviews")
public class InterviewController {
    @Autowired
    private UserService userService;
    @Autowired
    private InterviewService interviewService;
    // 创建面试记录
    @PostMapping("create")
    public Result createInterview(@RequestHeader String token, @RequestParam String position){
        Result result =interviewService.createInterview(token, position);
        return result;
    }
    // 面试开始
    @PostMapping("start")
    public Result startinterview(@RequestHeader String token){
        Result result =interviewService.startInterview(token);
        return result;
    }
    // AI出题
    @GetMapping("question")
    public Result getQuestion(@RequestHeader String token){
        Result result =interviewService.generateInterviewQuestion(token);
        return result;
    }
    // 回答问题
    @PostMapping("question")
    public Result answerQuestion(@RequestHeader String token, @RequestParam("videoFile") MultipartFile videoFile){
        Result result =interviewService.answerInterviewquestion(token, videoFile);
        return result;
    }
//    @PostMapping("complete")
//    public Result completeInterview(@RequestHeader String token, @RequestParam String position){
//        Result result =interviewService.completeInterview(token, position);
//        return result;
//    }


}
