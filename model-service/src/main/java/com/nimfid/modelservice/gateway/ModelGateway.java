package com.nimfid.modelservice.gateway;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimfid.commons.request.OrgCreationDto;
import com.nimfid.commons.request.OrgUpdateDto;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.response.MemberDashboardContent;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.modelservice.data.OrganizationModel;
import com.nimfid.modelservice.service.ModelDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/model")
@RequiredArgsConstructor
@Slf4j
public class ModelGateway {

    @Autowired
    private final ModelDBService modelDBService;

    @GetMapping("/health-check")
    public ResponseEntity<CustomResponse> healthCheck() {
        CustomResponse response = CustomResponse.builder()
                .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                .timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                .message("Hello").success(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> addOrganization(@RequestBody @Valid final OrgCreationDto orgCreationDto,
                                                    @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Starting to add organization");
            final OrganizationModel organizationModel = modelDBService.addOrganization(orgCreationDto, authUserDetails);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.CREATED.value()).status(HttpStatus.CREATED)
                    .message("Organization Added Successfully.").success(true)
                    .data(organizationModel)
                    .build();
            log.info("Organization Added Successfully.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (final Exception e) {
            final String message = "Unable to add organization to the database";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<CustomResponse> getDashboardStats(@RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Starting to get dashboard stats");
            final MemberDashboardContent dashboardContent = modelDBService.getDashboardStats(authUserDetails);
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

    @PutMapping("/update/{orgId}")
    public ResponseEntity<CustomResponse> updateOrganization(@NotNull @PathVariable("orgId") final Long orgId,
                                                             @RequestBody final OrgUpdateDto orgUpdateDto,
                                                          @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Starting to update organization");
            final OrganizationModel organizationModel = modelDBService.updateOrganization(orgId, orgUpdateDto, authUserDetails);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Organization updated Successfully.").success(true)
                    .data(organizationModel)
                    .build();
            log.info("Organization updated Successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to update organization";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/fetch-orgs")
    public ResponseEntity<CustomResponse> fetchOrganizations(@RequestParam final int page, @RequestParam final int size,
                                                    @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Starting to get organization");
            final PageResponse pageResponse = modelDBService.fetchOrganizations(page, size, authUserDetails);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Organization fetched Successfully.").success(true)
                    .data(pageResponse)
                    .build();
            log.info("Organization fetched Successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to add organization to the database";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/delete/{orgId}")
    public ResponseEntity<CustomResponse> deleteOrganization(@NotNull @PathVariable("orgId") final Long orgId,
                                                             @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Starting to delete organization");
            modelDBService.deleteOrg(orgId, authUserDetails);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Organization deleted Successfully.").success(true)
                    .build();
            log.info("Organization deleted Successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to delete organization from the database";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }


    private UserDetails extractUserDetailsFromHeader(final HttpHeaders httpHeaders) throws JsonProcessingException {
        String authenticatedUser = Objects.requireNonNull(httpHeaders.get("authenticatedUser")).get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(authenticatedUser, UserDetails.class);
    }

}
