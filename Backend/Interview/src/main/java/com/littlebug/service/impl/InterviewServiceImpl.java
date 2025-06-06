package com.littlebug.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.mapper.StudentMapper;
import com.littlebug.pojo.Interview;
import com.littlebug.pojo.Student;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import com.littlebug.utils.JwtHelper;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 种昊阳
* @description 针对表【interview(面试记录表)】的数据库操作Service实现
* @createDate 2025-06-05 13:36:52
*/
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview>
    implements InterviewService{
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private  InterviewMapper interviewMapper;

    @Override
    public Result init(String token, Integer positionType) {
        if(positionType < 0 || positionType >= 12){
            return Result.build("PositionType Not Exist", ResultCodeEnum.PARAM_ERROR);
        }
        //获取token对应的用户
        int studentId = jwtHelper.getUserId(token).intValue();
        //3.查询数据
        Student student = studentMapper.selectById(studentId);
        Interview interview = new Interview();
        interview.setPositionName("人工智能-技术岗");
        interview.setPositionType("技术岗");
        interview.setStatus(0);
        interview.setStudentId((long) studentId);
        interview.setCreateTime(new Date());
        interview.setEndTime(null);
        interviewMapper.insert(interview);
        return Result.ok(null);
    }
}




