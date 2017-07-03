package com.skunkworks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * stole on 03.07.17.
 */
@Configuration
@EnableConfigurationProperties
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    DataSource dataSource() {
        return new DriverManagerDataSource();
    }
}
