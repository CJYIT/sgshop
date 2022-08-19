package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/****启动类
 * @Author:cjy
 * @Description: TODO
 * @Date 2022/8/12 11:17
 * 启动类中开启feign
 * 修改CanalApplication，添加@EnableFeignClients注解
 *****/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
//启用canal
@EnableCanalClient
@EnableFeignClients(basePackages = "com.changgou.content.feign")
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}