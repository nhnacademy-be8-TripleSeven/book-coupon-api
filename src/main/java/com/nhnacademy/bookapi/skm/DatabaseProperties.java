package com.nhnacademy.bookapi.skm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {
    private String url;
    private String userName;
    private String password;
    private int initialSize;
    private int maxTotal;
    private int minIdle;
    private int maxIdle;
    private int maxWait;
}
