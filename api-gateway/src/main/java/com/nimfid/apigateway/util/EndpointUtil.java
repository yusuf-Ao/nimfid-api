package com.nimfid.apigateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.function.Predicate;

public class EndpointUtil {

    public static Predicate<ServerHttpRequest> isApiSecured() {
        final List<String> openEndpoints = List.of("/api/v1/auth/user-login", "/api/v1/auth/system-login",
                "/api/v1/user/register","/api/v1/user/verify-account",
                "/api/v1/user/health-check", "/api/v1/user/email-availability",
                "/api/v1/user/resend-code", "/api/v1/user/forgot-password",
                "/api/v1/user/reset-password");
        return r -> openEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));
    }
}
