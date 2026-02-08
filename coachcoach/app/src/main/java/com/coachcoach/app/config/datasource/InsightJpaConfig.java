package com.coachcoach.app.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.coachcoach.insight.repository",
        entityManagerFactoryRef = "insightEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class InsightJpaConfig {

    @Bean(name = "insightEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean insightEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("insightDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.coachcoach.insight.domain")
                .persistenceUnit("insight")
                .properties(JpaProperties.getHibernateProperties())
                .jta(true)
                .build();
    }
}