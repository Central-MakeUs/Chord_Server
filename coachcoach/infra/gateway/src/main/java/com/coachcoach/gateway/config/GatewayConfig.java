package com.coachcoach.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service",
                        p -> p.path("/api/auth/**")
                                .uri("lb://auth-service"))
                .route("user-store-service",
                        p -> p.path("/api/user-store/**")
                                .uri("lb://user-store-service"))
                .route("catalog-service",
                        p -> p.path("/api/catalog/**")
                                .uri("lb://catalog-service"))
                .route("insight-service",
                        p -> p.path("/api/insight/**")
                                .uri("lb://insight-service"))
                .build();
    }
}
