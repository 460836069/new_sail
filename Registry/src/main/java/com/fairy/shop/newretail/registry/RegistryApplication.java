package com.fairy.shop.newretail.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer  //标注此类是个注册中心
public class RegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class,args);
    }
}
