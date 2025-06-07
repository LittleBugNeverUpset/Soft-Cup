package com.littlebug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public Result init(String token, Integer position) {
        if(position < 0 || position >= 12){
            return Result.build("PositionType Not Exist", ResultCodeEnum.PARAM_ERROR);
        }
        // 暂定题目中的四种领域 positionType（i = 0.人工智能、1.大数据、2.物联网、3.智能系统）三种岗位positionName（j = 0.技术岗、1.运维测试岗、2.产品岗）
        String [] PostionTypes= {"人工智能","大数据","物联网","智能系统"};
        String [] PositionNames = {"技术岗","运维测试岗","产品岗"};

        int positionType = position / 4;
        int positionName = position % 4;
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

    @Override
    public Result start(String token) {
        //获取token对应的用户
        int studentId = jwtHelper.getUserId(token).intValue();
        //3.查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getStudentId, studentId);
        queryWrapper.eq(Interview::getStatus, 0);
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if (interview != null) {
            interview.setStatus(1);
            interview.setStartTime(new Date());
            interviewMapper.updateById(interview);
            return Result.ok(true);
        }
        return Result.build(false, ResultCodeEnum.PROCESS_ERROR);
    }
}




