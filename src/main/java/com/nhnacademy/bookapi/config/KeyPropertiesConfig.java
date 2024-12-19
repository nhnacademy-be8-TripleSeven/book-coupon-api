package com.nhnacademy.bookapi.config;

import com.nhnacademy.bookapi.skm.KeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeyProperties.class)
public class KeyPropertiesConfig {
}