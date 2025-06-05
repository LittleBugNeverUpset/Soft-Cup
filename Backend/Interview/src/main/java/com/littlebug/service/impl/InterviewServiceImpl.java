package com.littlebug.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.pojo.Interview;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import org.springframework.stereotype.Service;

/**
* @author 种昊阳
* @description 针对表【interview(面试记录表)】的数据库操作Service实现
* @createDate 2025-06-05 13:36:52
*/
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview>
    implements InterviewService{

}




