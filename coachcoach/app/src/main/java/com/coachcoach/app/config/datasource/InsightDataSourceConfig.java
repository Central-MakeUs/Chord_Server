package com.coachcoach.app.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Profile("prod")
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
        basePackages = "com.coachcoach.insight.repository",
        entityManagerFactoryRef = "insightEntityManagerFactory",
        transactionManagerRef = "insightTransactionManager"
)
public class InsightDataSourceConfig {
    
    @Bean
    @ConfigurationProperties("app.datasource.insight")
    public DataSourceProperties insightDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public HikariDataSource insightDataSource(
            @Qualifier("insightDataSourceProperties") DataSourceProperties props
    ) {
        HikariDataSource dataSource = props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        HikariConfigProperties.applyCommonConfig(dataSource);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean insightEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("insightDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.coachcoach.insight.domain")
                .persistenceUnit("insight")
                .properties(JpaProperties.getHibernateProperties())
                .build();
    }

    @Bean
    public PlatformTransactionManager insightTransactionManager(
            @Qualifier("insightEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
