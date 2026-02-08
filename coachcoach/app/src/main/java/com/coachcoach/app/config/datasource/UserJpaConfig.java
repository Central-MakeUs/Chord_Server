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
        basePackages = "com.coachcoach.user.repository",
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class UserJpaConfig {

    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.coachcoach.user.domain")
                .persistenceUnit("user")
                .properties(JpaProperties.getHibernateProperties())
                .jta(true)
                .build();
    }
}
