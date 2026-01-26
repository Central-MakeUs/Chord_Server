package com.coachcoach.config.datasource;

import com.coachcoach.config.HikariConfigProperties;
import com.coachcoach.config.JpaProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Profile("prod")
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
        basePackages = "com.coachcoach.catalog.domain.repository",
        entityManagerFactoryRef = "catalogEntityManagerFactory",
        transactionManagerRef = "catalogTransactionManager"
)
public class CatalogDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.catalog")
    public DataSourceProperties catalogDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public HikariDataSource catalogDataSource(
            @Qualifier("catalogDataSourceProperties") DataSourceProperties props
    ) {
        HikariDataSource dataSource = props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        HikariConfigProperties.applyCommonConfig(dataSource);

        return dataSource;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean catalogEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("catalogDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.coachcoach.catalog.domain.entity")
                .persistenceUnit("catalog")
                .properties(JpaProperties.getHibernateProperties())
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager catalogTransactionManager(
            @Qualifier("catalogEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
