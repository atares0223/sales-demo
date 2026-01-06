package com.waaw.user.conf;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "salesManagerFactory",
        basePackages = {"com.waaw.user.repository"}
        , transactionManagerRef = "salesTransactionManger"
)
@RequiredArgsConstructor
public class SalesSourceConfiguration {
    @Bean(name = "salesManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("salesSource") DataSource salesSource) {
        return entityManagerFactoryBuilder
                .dataSource(salesSource)
                .packages("com.waaw.user")
                .persistenceUnit("salesSource")
                .build();

    }

    @Bean(name = "salesTransactionManger")
    public PlatformTransactionManager transactionManager(@Qualifier("salesManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }


}