package com.changgou.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/****
 * @Author:cjy
 * @Description: com.changgou.user
 * @Date
 *****/
@SpringBootApplication
@EnableEurekaClient  //告示注册中心需要被注册
//mapper扫描 用通用的ampper扫描器
@MapperScan("com.changgou.user.dao")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
