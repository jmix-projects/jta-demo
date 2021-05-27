package com.company.jtatest.jta.datasource.local;

import io.jmix.autoconfigure.data.JmixLiquibaseCreator;
import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.data.impl.JmixEntityManagerFactoryBean;
import io.jmix.data.impl.JmixTransactionManager;
import io.jmix.data.impl.liquibase.LiquibaseChangeLogProcessor;
import io.jmix.data.persistence.DbmsSpecifics;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class LocalStoreConfiguration {

    @Bean
    @ConfigurationProperties("local.datasource")
    DataSourceProperties localDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    DataSource localDataSource(@Qualifier("localDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean localEntityManagerFactory(@Qualifier("localDataSource") DataSource dataSource,
                                                                     JpaVendorAdapter jpaVendorAdapter,
                                                                     DbmsSpecifics dbmsSpecifics,
                                                                     JmixModules jmixModules,
                                                                     Resources resources) {
        return new JmixEntityManagerFactoryBean("local", dataSource, jpaVendorAdapter, dbmsSpecifics, jmixModules, resources);
    }

    @Bean
    JpaTransactionManager localTransactionManager(@Qualifier("localEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JmixTransactionManager("local", entityManagerFactory);
    }

    @Bean
    public SpringLiquibase localLiquibase(@Qualifier("localDataSource") DataSource dataSource, LiquibaseChangeLogProcessor processor) {
        return JmixLiquibaseCreator.create(dataSource, new LiquibaseProperties(), processor, "local");
    }
}
