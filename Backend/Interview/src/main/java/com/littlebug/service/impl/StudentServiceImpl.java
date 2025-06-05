package com.littlebug.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.pojo.Student;
import com.littlebug.service.StudentService;
import com.littlebug.mapper.StudentMapper;
import com.littlebug.utils.JwtHelper;
import com.littlebug.utils.MD5Util;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author 种昊阳
* @description 针对表【student(学生信息表)】的数据库操作Service实现
* @createDate 2025-06-05 13:36:52
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private  StudentMapper studentMapper;

    @Override
    public Result login(Student student) {
        LambdaQueryWrapper<Student>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentNo,student.getStudentNo());
        Student loginStudent = studentMapper.selectOne(queryWrapper);
        if (loginStudent == null) {
            return  Result.build(null, ResultCodeEnum.STUDENT_NUMBER_NOT_EXIST);
        }
        if(
                !StringUtils.isEmpty(student.getEncryptedPwd()) &&
                        loginStudent.getEncryptedPwd().equals((MD5Util.encrypt(student.getEncryptedPwd())))
        ){
            //账号密码正确
            //根据用户唯一标识生成token
            String token = jwtHelper.createToken(Long.valueOf(loginStudent.getId()));
            Map data = new HashMap();
            data.put("token",token);

            return Result.ok(data);
        }

        //密码错误
        return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);

    }
}




