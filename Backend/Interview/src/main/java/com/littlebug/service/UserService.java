package com.littlebug.service;

import com.littlebug.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.littlebug.utils.Result;

/**
* @author 种昊阳
* @description 针对表【user】的数据库操作Service
* @createDate 2025-06-14 12:13:52
*/
public interface UserService extends IService<User> {

    Result checkUserName(String userName);

    Result regist(User user);

    Result login(User user);

    Result getUserInfo(String token);
}
