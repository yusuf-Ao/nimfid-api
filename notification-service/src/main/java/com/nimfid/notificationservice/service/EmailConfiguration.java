package com.nimfid.notificationservice.service;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
public class EmailConfiguration {

    @Value("${spring.mail.username}")
    private  String     username;
    @Value("${spring.mail.password}")
    private  String     password;
}
