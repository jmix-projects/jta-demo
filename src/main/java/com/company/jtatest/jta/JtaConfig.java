package com.company.jtatest.jta;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import io.jmix.data.impl.jta.JmixJtaTransactionController;
import io.jmix.eclipselink.impl.JmixEclipselinkJtaTransactionManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
public class JtaConfig {

    @Bean
    JmixJtaTransactionController jtaTransactionController() {
        return new JmixJtaTransactionController();
    }

    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public TransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean(name = {"transactionManager", "ordersTransactionManager", "jmsTransactionManager"})
    @DependsOn({"userTransaction", "atomikosTransactionManager"})
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();
        TransactionManager atomikosTransactionManager = atomikosTransactionManager();
        return new JmixEclipselinkJtaTransactionManager("transactionManager", userTransaction, atomikosTransactionManager);
    }

}
