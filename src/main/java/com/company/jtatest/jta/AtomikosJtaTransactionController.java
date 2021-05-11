package com.company.jtatest.jta;

import org.eclipse.persistence.transaction.JTATransactionController;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.transaction.TransactionManager;

@Component
public class AtomikosJtaTransactionController extends JTATransactionController implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public AtomikosJtaTransactionController() {
    }

    public AtomikosJtaTransactionController(TransactionManager transactionManager) {
        super(transactionManager);
    }

    protected TransactionManager acquireTransactionManager() throws Exception {
        if (transactionManager == null && ctx != null) {
            transactionManager = ctx.getBean(TransactionManager.class);
        }
        return transactionManager;
    }

}
