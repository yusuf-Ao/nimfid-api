package com.nimfid.persistenceservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.nimfid.persistenceservice",
                "com.nimfid.commons"
        }
)
@EnableFeignClients
@EnableEurekaClient
@EntityScan(
        basePackages = {
                "com.nimfid.commons",
                "com.nimfid.persistenceservice"
        }
)
public class PersistenceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersistenceServiceApplication.class, args);
    }
}
