package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/****
 * @Author:cjy
 * @Description: com.changgou
 * @Date
 *****/
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }


    /***创建用户唯一标识，使用IP作为用户唯一标识，根据ip进行限流操作
     * IP限流
     * @return
     */
    @Bean(name="ipKeyResolver")   //把这个容器放到容器中去，取名为ipKeyResolver
    public KeyResolver userKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                /*//获取远程客户端IP  exchange.getRequest().getRemoteAddress().getHostString();这就能获取到ip了
                String hostName = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                System.out.println("hostName:"+hostName);
                return Mono.just(hostName);*/
                String ip = exchange.getRequest().getRemoteAddress().getHostString();
                System.out.println("ip:"+ip);
                return Mono.just(ip);
            }
        };
    }
}
