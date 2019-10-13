package com.feri.shop.newretail.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication   //为啥不加 @EnableDiscoveryClient
public class MsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgApplication.class,args);
    }
}
