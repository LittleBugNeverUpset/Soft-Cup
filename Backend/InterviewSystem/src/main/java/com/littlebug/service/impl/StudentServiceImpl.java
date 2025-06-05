package com.littlebug.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.pojo.Student;
import com.littlebug.service.StudentService;
import com.littlebug.mapper.StudentMapper;
import org.springframework.stereotype.Service;

/**
* @author 种昊阳
* @description 针对表【student(学生信息表)】的数据库操作Service实现
* @createDate 2025-06-05 13:36:52
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{

}




