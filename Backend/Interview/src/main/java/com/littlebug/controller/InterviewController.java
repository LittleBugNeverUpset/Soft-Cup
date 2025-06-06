package com.littlebug.controller;

import com.littlebug.service.InterviewService;
import com.littlebug.service.StudentService;
import com.littlebug.utils.Result;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 面试控制器示例
@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private InterviewService interviewService;


    @PostMapping("init")
    public Result InitInterview(@RequestHeader String token , @RequestParam  Integer positionType ) {
        // 暂定题目中的四种领域（i = 0.人工智能、1.大数据、2.物联网、3.智能系统）三种岗位（j = 0.技术岗、1.运维测试岗、2.产品岗）
        //positionType = i * 4 + j;
        Result  result = interviewService.init(token ,positionType);
        System.out.println("result = " + result);
        return result;

    }
//
//    @GetMapping("/{id}/current-question")
//    public Response<QuestionDTO> getCurrentQuestion(
//            @PathVariable Long id) {
//        // 实现逻辑
//    }
//
//    @PostMapping("/{id}/answers")
//    public Response<Void> submitAnswer(
//            @PathVariable Long id,
//            @RequestBody AnswerSubmission request) {
//        // 实现逻辑
//    }
//
//    @GetMapping("/{id}/report")
//    public Response<ReportDTO> getReport(
//            @PathVariable Long id) {
//        // 实现逻辑
//    }
}
