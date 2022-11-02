package com.nimfid.modelservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.nimfid.modelservice",
                "com.nimfid.commons"
        }
)
@EnableFeignClients(
        basePackages = "com.nimfid.commons"
)
@EnableEurekaClient
public class ModelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelServiceApplication.class, args);
    }
}
