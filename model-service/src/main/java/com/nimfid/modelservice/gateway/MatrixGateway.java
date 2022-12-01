package com.nimfid.modelservice.gateway;


import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.response.SystemDashboardContent;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.modelservice.service.MatrixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matrix")
@RequiredArgsConstructor
@Slf4j
public class MatrixGateway {

    @Autowired
    private final MatrixService matrixService;

    @Operation(summary = "Get All Orgs",
            description = "To get detailed list of all registered orgs(Accessible by System User Only)",
            security = { @SecurityRequirement(name = "Bearer Token") })
    @GetMapping("/fetch-orgs")
    public ResponseEntity<CustomResponse> getAllOrgs(@RequestParam final int page, @RequestParam final int size) {
        try {
            log.info("Trying to fetch all orgs");
            final PageResponse userPageResponse = matrixService.getAllOrgs(page, size);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Orgs fetched Successfully").success(true)
                    .data(userPageResponse)
                    .build();
            log.info("Orgs fetched Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to fetch organizations";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "System User Dashboard",
            description = "To fetch dashboard content of the logged in user(Accessible by System Users Only)",
            security = { @SecurityRequirement(name = "Bearer Token") })
    @GetMapping("/statistics")
    public ResponseEntity<CustomResponse> getDashboardStats() {
        try {
            log.info("Starting to get dashboard stats");
            final SystemDashboardContent dashboardContent = matrixService.getDashboardStats();
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Dashboard stats fetched successfully.").success(true)
                    .data(dashboardContent)
                    .build();
            log.info("Dashboard stats fetched successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to fetch dashboard stats";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
