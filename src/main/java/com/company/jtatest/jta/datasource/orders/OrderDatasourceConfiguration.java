package com.company.jtatest.jta.datasource.orders;

import com.company.jtatest.jta.AtomikosServerPlatform;
import io.jmix.autoconfigure.data.JmixLiquibaseCreator;
import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.data.impl.JmixEntityManagerFactoryBean;
import io.jmix.data.impl.liquibase.LiquibaseChangeLogProcessor;
import io.jmix.data.persistence.DbmsSpecifics;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@DependsOn("ordersTransactionManager")
public class OrderDatasourceConfiguration {

    @Autowired
    OrdersDatasourceProperties dsConfig;

    @Bean(name = "ordersDataSource")
    @Qualifier("orders")
    public DataSource ordersDataSource() {
        PGXADataSource ds = new PGXADataSource();
        ds.setURL(dsConfig.getJdbcUrl());
        ds.setUser(dsConfig.getUsername());
        ds.setPassword(dsConfig.getPassword());

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName("cubaXADs/orders");
        atomikosDataSourceBean.setXaDataSource(ds);
        atomikosDataSourceBean.setMaxPoolSize(100);
        return atomikosDataSourceBean;
    }

    @Bean
    @DependsOn({"ordersTransactionManager", "ordersDataSource"})
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory(@Qualifier("orders") DataSource ordersDataSource,
                                                                             JpaVendorAdapter jpaVendorAdapter,
                                                                             DbmsSpecifics dbmsSpecifics,
                                                                             JmixModules jmixModules,
                                                                             Resources resources) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("eclipselink.target-server", AtomikosServerPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        LocalContainerEntityManagerFactoryBean entityManager =
                new JmixEntityManagerFactoryBean("orders", ordersDataSource, jpaVendorAdapter, dbmsSpecifics, jmixModules, resources);
        entityManager.setJtaDataSource(ordersDataSource);
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }

    @Bean
    public SpringLiquibase ordersLiquibase(LiquibaseChangeLogProcessor processor) {
        return JmixLiquibaseCreator.create(ordersDataSource(), new LiquibaseProperties(), processor, "orders");
    }


}
