package com.company.jtatest.jta;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

@Configuration
@DependsOn("jmsTransactionManager")
public class JmsConfig {
    @Bean(name = "jmsConnectionFactory")
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory("vm://localhost?broker.persistent=false");
        activeMQXAConnectionFactory.setTrustAllPackages(true);

        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
        atomikosConnectionFactoryBean.setLocalTransactionMode(false);
        atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
        return atomikosConnectionFactoryBean;
    }

    @Bean
    @DependsOn({"jmsTransactionManager", "jmsConnectionFactory"})
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer conf,
                                                                      ConnectionFactory jmsConnectionFactory,
                                                                      PlatformTransactionManager jmsTransactionManager) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        conf.configure(factory, jmsConnectionFactory);
        factory.setTransactionManager(jmsTransactionManager);
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.DUPS_OK_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    @DependsOn("jmsConnectionFactory")
    public JmsTemplate JmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setDefaultDestinationName("test");
        jmsTemplate.setReceiveTimeout(100);
        return jmsTemplate;
    }
}
