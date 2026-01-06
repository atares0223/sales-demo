package com.waaw.user.conf;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSources {

    @Bean(name="salesSource")
    @ConfigurationProperties(prefix = "spring.datasource.sales")
    public DataSource sales() {
        // Filled up with the properties specified about thanks to Spring Boot black magic
        return new HikariDataSource();
    }

}
