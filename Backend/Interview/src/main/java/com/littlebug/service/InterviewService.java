package com.littlebug.service;

import com.littlebug.pojo.Interview;
import com.baomidou.mybatisplus.extension.service.IService;
import com.littlebug.utils.Result;

/**
* @author 种昊阳
* @description 针对表【interview(面试记录表)】的数据库操作Service
* @createDate 2025-06-05 13:36:52
*/
public interface InterviewService extends IService<Interview> {

    Result init(String token, Integer position);

    Result start(String token);
}
