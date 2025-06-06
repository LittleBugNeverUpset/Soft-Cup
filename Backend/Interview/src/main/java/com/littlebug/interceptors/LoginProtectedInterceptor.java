package com.littlebug.interceptors;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlebug.utils.JwtHelper;
import com.littlebug.utils.Result;
import com.littlebug.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginProtectedInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        boolean expiration = jwtHelper.isExpiration(token);

        if(!expiration) {
            return true;
        } else {
            Result result = Result.build(null,ResultCodeEnum.NOTLOGIN);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);
            response.getWriter();
            return false;
        }
    }
}
