package com.waaw.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages={"com.waaw.feign.api"})
//@EnableTransactionManagement
@ComponentScan(value = {"com.waaw.common.bean","com.waaw.merchant","com.waaw.feign.api"})
public class MerchantApplication {
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }

}