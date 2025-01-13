package com.nhnacademy.bookapi.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nhnacademy.bookapi.securekeymanager.SecureKeyManagerService;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

class DataSourceConfigTest {
    @Test
    void testDataSourceConfiguration() {
        SecureKeyManagerService mockService = mock(SecureKeyManagerService.class);
        when(mockService.fetchSecretFromKeyManager()).thenReturn("jdbc:mysql://url\nusername\npassword");

        DataSourceConfig config = new DataSourceConfig(mockService);
        DataSource dataSource = config.dataSource();

        assertNotNull(dataSource);
        assertEquals("com.mysql.cj.jdbc.Driver", ((BasicDataSource) dataSource).getDriverClassName());
        assertEquals("jdbc:mysql://url", ((BasicDataSource) dataSource).getUrl());
        assertEquals("username", ((BasicDataSource) dataSource).getUsername());
    }
}