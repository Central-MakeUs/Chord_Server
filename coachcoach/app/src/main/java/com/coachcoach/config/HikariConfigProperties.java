package com.coachcoach.config;

import com.zaxxer.hikari.HikariDataSource;

public class HikariConfigProperties {
    public static void applyCommonConfig(HikariDataSource dataSource) {
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setConnectionTestQuery("SELECT 1");
    }
}
