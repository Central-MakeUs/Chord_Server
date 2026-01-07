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
                                .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/${segment}"))
                                .uri("lb://auth-service"))
                .route("user-store-service",
                        p -> p.path("/api/user-store/**")
                                .filters(f -> f.rewritePath("/api/user-store/(?<segment>.*)", "/${segment}"))
                                .uri("lb://user-store-service"))
                .route("catalog-service",
                        p -> p.path("/api/catalog/**")
                                .filters(
                                        f -> f
                                                .rewritePath("/api/catalog/(?<segment>.*)", "/${segment}")
                                                .addRequestHeader("userId", "1")
                                )
                                .uri("lb://catalog-service"))
                .route("insight-service",
                        p -> p.path("/api/insight/**")
                                .filters(f -> f.rewritePath("/api/insight/(?<segment>.*)", "/${segment}"))
                                .uri("lb://insight-service"))
                .build();
    }
}
