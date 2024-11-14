package com.bolz.elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Configuration
public class FallbackConfiguration {
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.GET("/user-fallback"), this::handleGetFallback)
                .andRoute(RequestPredicates.POST("/user-fallback"), this::handlePostFallback);
    }

    private Mono<ServerResponse> handlePostFallback(ServerRequest serverRequest) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body(Mono.fromCallable(() -> "Service Unavailable"), String.class);
    }

    private Mono<ServerResponse> handleGetFallback(ServerRequest serverRequest) {
        return ServerResponse.ok().body(Mono.empty(), String.class);
    }
}
