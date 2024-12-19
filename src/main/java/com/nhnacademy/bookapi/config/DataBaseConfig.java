package com.nhnacademy.bookapi.config;

import com.nhnacademy.bookapi.skm.DatabaseProperties;
import jakarta.activation.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@RequiredArgsConstructor
//public class DataBaseConfig {
//
//    private final KeyConfig keyConfig;
//    private final DatabaseProperties databaseProperties;
//
//    @Bean
//    public DataSource dataSources() {
//        BasicDataSource dataSource = new BasicDataSource();
//
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl(keyConfig.keyStore(databaseProperties.getUrl()));  // 복호화된 URL
//        dataSource.setUsername(keyConfig.keyStore(databaseProperties.getUserName()));  // 복호화된 사용자 이름
//        dataSource.setPassword(keyConfig.keyStore(databaseProperties.getPassword()));  // 복호화된 비밀번호
//
//        dataSource.setInitialSize(databaseProperties.getInitialSize());
//        dataSource.setMaxTotal(databaseProperties.getMaxTotal());
//        dataSource.setMinIdle(databaseProperties.getMinIdle());
//        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
//        dataSource.setMaxWaitMillis(databaseProperties.getMaxWait());
//        dataSource.setTestOnBorrow(true);
//        dataSource.setTestOnReturn(true);
//        dataSource.setTestWhileIdle(true);
//
//        return (DataSource) dataSource;
//    }
//}
