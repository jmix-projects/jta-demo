package com.company.jtatest.jta.datasource.orders;

import io.jmix.autoconfigure.data.JmixLiquibaseCreator;
import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.data.impl.JmixJtaEntityManagerFactoryBean;
import io.jmix.data.impl.liquibase.LiquibaseChangeLogProcessor;
import io.jmix.data.persistence.DbmsSpecifics;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@DependsOn("ordersTransactionManager")
public class OrderDatasourceConfiguration {
    @Bean
    @ConfigurationProperties("orders.datasource")
    DataSourceProperties ordersDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "ordersDataSource")
    public DataSource ordersDataSource(@Qualifier("ordersDataSourceProperties") DataSourceProperties dataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setURL(dataSourceProperties.getUrl());
        ds.setUser(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName("cubaXADs/orders");
        atomikosDataSourceBean.setXaDataSource(ds);
        atomikosDataSourceBean.setMaxPoolSize(100);
        return atomikosDataSourceBean;
    }

    @Bean
    @DependsOn({"ordersTransactionManager", "ordersDataSource"})
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory(@Qualifier("ordersDataSource") DataSource ordersDataSource,
                                                                             JpaVendorAdapter jpaVendorAdapter,
                                                                             DbmsSpecifics dbmsSpecifics,
                                                                             JmixModules jmixModules,
                                                                             Resources resources) {
        return new JmixJtaEntityManagerFactoryBean("orders",
                ordersDataSource,
                jpaVendorAdapter,
                dbmsSpecifics,
                jmixModules,
                resources);
    }

    @Bean
    public SpringLiquibase ordersLiquibase(@Qualifier("ordersDataSource") DataSource ordersDataSource, LiquibaseChangeLogProcessor processor) {
        return JmixLiquibaseCreator.create(ordersDataSource, new LiquibaseProperties(), processor, "orders");
    }
}
