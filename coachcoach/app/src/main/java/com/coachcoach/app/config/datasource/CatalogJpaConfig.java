package com.coachcoach.app.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.coachcoach.catalog.repository",
        entityManagerFactoryRef = "catalogEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class CatalogJpaConfig {

    @Bean(name = "catalogEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean catalogEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("catalogDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.coachcoach.catalog.domain")
                .persistenceUnit("catalog")
                .properties(JpaProperties.getHibernateProperties())
                .jta(true)
                .build();
    }
}