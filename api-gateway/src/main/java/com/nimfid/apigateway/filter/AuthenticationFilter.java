package com.nimfid.apigateway.filter;

import com.nimfid.apigateway.service.FraudCheck;
import com.nimfid.apigateway.util.EndpointUtil;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.token.AuthenticationTokenDetails;
import com.nimfid.commons.token.AuthenticationTokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {
    @Autowired
    private final FraudCheck fraudCheck;
    @Autowired
    private final AuthenticationTokenParser authenticationTokenParser;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();

        if (EndpointUtil.isApiSecured().test(request)) {
            log.info("Checking for authorization token");
            final boolean isAuthorizationHeader = request.getHeaders().containsKey(AUTHORIZATION);
            if (!isAuthorizationHeader) {
                final String message = "Request does not have relevant authorization";
                response.setStatusCode(UNAUTHORIZED);
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                log.error(message);
                return exchange.getResponse().writeWith(Flux.just(buffer));
            }
            try {
                log.info("Checking if authorization type is Bearer Token");
                final String authHeader = Objects.requireNonNull(request.getHeaders().get(AUTHORIZATION)).get(0);
                final String[] parts = authHeader.split(" ");
                if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                    final String message = "Incorrect Auth Structure";
                    response.setStatusCode(UNAUTHORIZED);
                    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    log.error(message);
                    return exchange.getResponse().writeWith(Flux.just(buffer));

                }

                if(!request.getURI().getPath().endsWith("/refresh-token")) {
                    log.info("Forwarding token for parsing and validation");
                    final String authenticationToken = parts[1];
                    final AuthenticationTokenDetails authenticationTokenDetails = authenticationTokenParser
                            .parseAccessToken(authenticationToken);
                    final Long userId = authenticationTokenDetails.getUserId();
                    final String uuid = authenticationTokenDetails.getUuid();
                    final Optional<UserDetails> userDetails = fraudCheck.verifyUserExistence(userId,uuid);
                    if (userDetails.isEmpty()) {
                        final String message = "User is Fraud";
                        response.setStatusCode(UNAUTHORIZED);
                        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        log.error(message);
                        return exchange.getResponse().writeWith(Flux.just(buffer));
                    }
                    final UserDetails user = userDetails.get();
                    log.info("Begin to mutate request with authenticated user details");
                    exchange.getRequest().mutate()
                            .header("authenticatedUser", user.toString());
                }

            } catch (final Exception e) {
                final String message = e.getMessage();
                response.setStatusCode(UNAUTHORIZED);
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                log.error(message);
                return exchange.getResponse().writeWith(Flux.just(buffer));
            }

        }
        return chain.filter(exchange);
    }
}
