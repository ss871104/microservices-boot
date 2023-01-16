package com.ss871104.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
    // http://localhost:8083/api/order/info/10001
    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f
                                .addRequestHeader("MyHeader", "MyURI")
                                .addRequestParameter("Param", "MyValue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/api/customer/**")
                        .uri("lb://customer"))
                .route(p -> p.path("/api/product/**")
                        .uri("lb://product"))
                .route(p -> p.path("/api/order/**")
                        .uri("lb://order"))
                .route(p -> p.path("/api/notification/**")
                        .uri("lb://notification"))
                .build();
    }
}
