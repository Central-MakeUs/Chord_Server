package com.coachcoach.config.datasource;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaProperties {

    public static Map<String, Object> getHibernateProperties() {
        Map<String, Object> properties = new HashMap<>();

        // DDL
        properties.put("hibernate.hbm2ddl.auto", "none");

        // NAMING
        properties.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        // Dialect
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // SQL Logging
        properties.put("hibernate.show_sql", false);

        // Performance
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.default_batch_fetch_size", "100");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");

        return properties;
    }
}
