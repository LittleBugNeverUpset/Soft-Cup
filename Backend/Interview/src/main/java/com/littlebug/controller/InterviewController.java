package com.littlebug.controller;

import com.littlebug.service.InterviewService;
import com.littlebug.service.UserService;
import com.littlebug.utils.Result;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// 面试控制器示例
@RestController
@RequestMapping("/api/interviews")
public class InterviewController {
    @Autowired
    private UserService userService;
    @Autowired
    private InterviewService interviewService;
    // 创建面试记录
    @PostMapping(value ="create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result createInterview(
            @RequestHeader String token,
            @RequestParam String position,
            @RequestPart("resume_file") MultipartFile PdfFile,
            @RequestPart("job_file") MultipartFile TxtFile) throws IOException {
        Result result =interviewService.createInterview(token, position,PdfFile,TxtFile);
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
