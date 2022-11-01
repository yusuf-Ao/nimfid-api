package com.nimfid.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimfid.apigateway.util.EndpointUtil;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.request.UserDetails;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    public AuthorizationFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();

            if (EndpointUtil.isApiSecured().test(request)) {
                log.info("Fetching Allowed Roles for current route");
                final Collection<UserRoles> rolesAllowed = config.getUserRoles();
                if (!request.getURI().getPath().endsWith("/refresh-token")) {
                    if (rolesAllowed.size() > 0 && !isAuthenticated(exchange)) {
                        final String message = "Authentication is required to proceed";
                        response.setStatusCode(UNAUTHORIZED);
                        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        log.error(message);
                        return exchange.getResponse().writeWith(Flux.just(buffer));
                    }
                    Collection<UserRoles> authorities = extractUserRoles(request);
                    for (final UserRoles role : rolesAllowed) {
                        if (isUserInRole(role.getUserRole(), authorities)) {
                            log.info("Access Granted");
                            return chain.filter(exchange);
                        }
                    }
                    final String message = "Access Denied";
                    response.setStatusCode(UNAUTHORIZED);
                    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    log.error(message);
                    return exchange.getResponse().writeWith(Flux.just(buffer));
                }
            }
            return chain.filter(exchange);
        };
    }

    private Collection<UserRoles> extractUserRoles(final ServerHttpRequest request) {
        try {
            String userObject = Objects.requireNonNull(request.getHeaders().get("authenticatedUser")).get(0);
            ObjectMapper objectMapper = new ObjectMapper();
            UserDetails userDetails = objectMapper.readValue(userObject, UserDetails.class);
            return userDetails.getUserRoles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isAuthenticated(final ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().get("authenticatedUser") != null;
    }

    private boolean isUserInRole(final String role, final Collection<UserRoles> userRoles) {
        return userRoles.contains(UserRoles.valueOf(role));
    }

    @Data
    public static class Config {
        private Collection<UserRoles> userRoles;

    }


    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("userRoles");
    }
}