package com.littlebug.service;

import com.littlebug.pojo.Interview;
import com.baomidou.mybatisplus.extension.service.IService;
import com.littlebug.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author 种昊阳
* @description 针对表【interview】的数据库操作Service
* @createDate 2025-06-14 12:13:52
*/
public interface InterviewService extends IService<Interview> {

    Result createInterview(String token, String position ,MultipartFile PdfFile, MultipartFile TxtFile) throws IOException;

    Result startInterview(String token);

    Result generateInterviewQuestion(String token) throws IOException;

    Result answerInterviewquestion(String token, MultipartFile videoFile,String answer);

    Result completeInterview(String token) throws IOException;
}
