package com.nimfid.modelservice.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
public class ModelConfigs {

    @Value("${api-keys.google.maps}")
    private String apiKey;

}
