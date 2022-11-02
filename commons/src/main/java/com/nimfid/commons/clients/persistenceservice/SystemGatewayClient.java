package com.nimfid.commons.clients.persistenceservice;


import com.nimfid.commons.response.SystemDashboardContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        value = "persistence-service",
        path = "/api/v1/system"
)
public interface SystemGatewayClient {

    @GetMapping("/partial-response-stats")
    ResponseEntity<SystemDashboardContent> getStatsPartial();

}
