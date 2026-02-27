package com.waaw.order.conf;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import io.seata.rm.datasource.DataSourceProxy;

@Configuration
public class DataSources {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.sales")
    public DataSourceProperties salesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "salesSource")
    public DataSource sales(DataSourceProperties salesDataSourceProperties) {
        HikariDataSource dataSource = salesDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
        return new DataSourceProxy(dataSource);
    }

}
