package com.coachcoach.app.config.datasource;

import lombok.Setter;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.jta.UserTransactionImp;

public class AtomikosJtaPlatform extends AbstractJtaPlatform {

    @Setter
    private static UserTransactionManager transactionManager;
    @Setter
    private static UserTransaction userTransaction;

    @Override
    protected TransactionManager locateTransactionManager() {
        return transactionManager;
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return userTransaction;
    }
}
