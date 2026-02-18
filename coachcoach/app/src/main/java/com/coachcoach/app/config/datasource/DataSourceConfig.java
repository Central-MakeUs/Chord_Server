package com.coachcoach.app.config.datasource;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Profile("prod")
@Configuration
public class DataSourceConfig {
    @Value("${DATASOURCE_HOST}")
    private String host;

    @Value("${DATASOURCE_PORT}")
    private String port;

    @Value("${DATASOURCE_USERNAME}")
    private String username;

    @Value("${DATASOURCE_PASSWORD}")
    private String password;

    @Value("${CATALOG_DB_NAME}")
    private String catalogDbName;

    @Value("${USER_DB_NAME}")
    private String userDbName;

    @Value("${INSIGHT_DB_NAME}")
    private String insightDbName;


    @Bean(name = "userDataSource")
    @Primary
    public DataSource userDataSource() {
        return createDataSource("userDB", userDbName);
    }

    @Bean(name = "catalogDataSource")
    public DataSource catalogDataSource() {
        return createDataSource("catalogDB", catalogDbName);
    }

    @Bean(name = "insightDataSource")
    public DataSource insightDataSource() {
        return createDataSource("insightDB", insightDbName);
    }

    private DataSource createDataSource(String uniqueResourceName, final String dbName) {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();

        ds.setUniqueResourceName(uniqueResourceName);
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        Properties props = new Properties();
        props.setProperty("serverName", host);
        props.setProperty("portNumber", port);
        props.setProperty("databaseName", dbName);
        props.setProperty("user", username);
        props.setProperty("password", password);
        ds.setXaProperties(props);

        ds.setMinPoolSize(5);
        ds.setMaxPoolSize(20);
        ds.setTestQuery("SELECT 1");

        return ds;
    }

    @Bean
    public UserTransaction userTransaction() throws SystemException {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(300);
        return userTransactionImp;
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(UserTransaction userTransaction)
            throws SystemException {
        UserTransactionManager atomikosTransactionManager = new UserTransactionManager();
        atomikosTransactionManager.setForceShutdown(false);

        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(atomikosTransactionManager);
        jtaTransactionManager.setUserTransaction(userTransaction);

        return jtaTransactionManager;
    }
}
