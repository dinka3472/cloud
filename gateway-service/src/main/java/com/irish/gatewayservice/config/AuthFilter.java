package com.irish.gatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AuthFilter implements GatewayFilter {

    private final RouterValidator validator;
    private final WebClient.Builder webClientBuilder;

    public AuthFilter(RouterValidator validator, WebClient.Builder webClientBuilder) {
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (validator.isSecured.test(request)) {
            if (authMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = resolveToken(request);

            return webClientBuilder.baseUrl("http://localhost:9001/auth")
                    .build()
                    .get()
                    .uri("/validate?token=" + token)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is4xxClientError()) {
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(responseBody -> chain.filter(exchange));
                    });
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    public String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getOrEmpty("Authorization").get(0);

        if (bearerToken != null && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
