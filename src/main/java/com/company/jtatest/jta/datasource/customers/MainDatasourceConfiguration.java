package com.company.jtatest.jta.datasource.customers;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.data.impl.JmixJtaEntityManagerFactoryBean;
import io.jmix.data.persistence.DbmsSpecifics;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@DependsOn("transactionManager")
public class MainDatasourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("dataSourceProperties") DataSourceProperties dataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setURL(dataSourceProperties.getUrl());
        ds.setUser(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName("cubaXADs/main");
        atomikosDataSourceBean.setXaDataSource(ds);
        atomikosDataSourceBean.setMaxPoolSize(100);

        return atomikosDataSourceBean;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    @DependsOn({"transactionManager", "dataSource"})
    public LocalContainerEntityManagerFactoryBean entityManager(DataSource dataSource,
                                                                JpaVendorAdapter jpaVendorAdapter,
                                                                DbmsSpecifics dbmsSpecifics,
                                                                JmixModules jmixModules,
                                                                Resources resources) {


        return new JmixJtaEntityManagerFactoryBean("main",
                dataSource,
                jpaVendorAdapter,
                dbmsSpecifics,
                jmixModules,
                resources);
    }
}
