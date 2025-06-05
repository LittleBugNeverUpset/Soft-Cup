package com.littlebug.service;

import com.littlebug.pojo.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.littlebug.utils.Result;

/**
* @author 种昊阳
* @description 针对表【student(学生信息表)】的数据库操作Service
* @createDate 2025-06-05 13:36:52
*/
public interface StudentService extends IService<Student> {

    /**
     * 登录需求
     * 地址: /user/login
     * 方式: post
     * 参数:
     *    {
     *     "username":"zhangsan", //用户名
     *     "userPwd":"123456"     //明文密码
     *    }
     * 返回:
     *   {
     *    "code":"200",         // 成功状态码
     *    "message":"success"   // 成功状态描述
     *    "data":{
     *         "token":"... ..." // 用户id的token
     *     }
     *  }
     *
     * 大概流程:
     *    1. 账号进行数据库查询 返回用户对象
     *    2. 对比用户密码(md5加密)
     *    3. 成功,根据userId生成token -> map key=token value=token值 - result封装
     *    4. 失败,判断账号还是密码错误,封装对应的枚举错误即可
     */
    Result login(Student student);
}
