package com.littlebug;

import com.baomidou.mybatisplus.autoconfigure.DdlApplicationRunner;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.littlebug", "com.littlebug.service"}) // 明确指定要扫描的包
@MapperScan("com.littlebug.mapper")
@ComponentScan(basePackages = {"com.littlebug.controller", "com.littlebug.config"})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
    }

    //配置mybatis-plus插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());  //乐观锁
        return interceptor;
    }

}
