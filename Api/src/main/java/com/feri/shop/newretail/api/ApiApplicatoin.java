package com.feri.shop.newretail.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients  //标志是基于Feign实现服务消费
public class ApiApplicatoin {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplicatoin.class,args);
    }
}
