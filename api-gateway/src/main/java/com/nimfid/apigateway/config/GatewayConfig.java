package com.nimfid.apigateway.config;


import com.nimfid.apigateway.filter.AuthenticationFilter;
import com.nimfid.apigateway.filter.AuthorizationFilter;
import com.nimfid.commons.enums.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Autowired
    private final AuthenticationFilter authenticationFilter;
    @Autowired
    private final AuthorizationFilter authorizationFilter;

    @Bean
    public RouteLocator routeLocator(final RouteLocatorBuilder builder) {

        return builder.routes()
                .route("persistence-service", r -> r.path("/api/v1/user/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .filter(memberUserFilter()))
                        .uri("lb://persistence-service"))
                .route("persistence-service", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .filter(allUserFilter()))
                        .uri("lb://persistence-service"))
                .route("persistence-service", r -> r.path("/api/v1/system/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .filter(systemUserFilter()))
                        .uri("lb://persistence-service"))
                .build();
    }

    private GatewayFilter memberUserFilter() {
        List<UserRoles> member_user = new ArrayList<>();
        member_user.add(UserRoles.MEMBER_USER);
        AuthorizationFilter.Config config = new AuthorizationFilter.Config();
        config.setUserRoles(member_user);
        return authorizationFilter.apply(config);
    }
    private GatewayFilter systemUserFilter() {
        List<UserRoles> system_user = new ArrayList<>();
        system_user.add(UserRoles.SYSTEM_USER);
        AuthorizationFilter.Config config = new AuthorizationFilter.Config();
        config.setUserRoles(system_user);
        return authorizationFilter.apply(config);
    }
    private GatewayFilter allUserFilter() {
        List<UserRoles> allUsers = new ArrayList<>();
        allUsers.add(UserRoles.MEMBER_USER);
        allUsers.add(UserRoles.SYSTEM_USER);
        AuthorizationFilter.Config config = new AuthorizationFilter.Config();
        config.setUserRoles(allUsers);
        return authorizationFilter.apply(config);
    }
}
