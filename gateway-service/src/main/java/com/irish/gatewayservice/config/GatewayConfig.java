package com.irish.gatewayservice.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthFilter filter) {
        return builder.routes()
                .route("course-service", r -> r.path("/course/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://course"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth"))
                .route("student-service", r -> r.path("/students/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://student"))
                .route("course-service", r -> r.path("/courses/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://course"))
                .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
