package com.nimfid.modelservice.gateway;


import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.modelservice.service.ModelDBService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Slf4j
public class PublicGateway {

    @Autowired
    private final ModelDBService modelDBService;

    @Operation(summary = "Fetch Orgs summary",
            description = "To get summarized list of all organizations for public search")
    @GetMapping("/find-org")
    public ResponseEntity<CustomResponse> fetchOrganizationsRecord(@RequestParam @Nullable final Integer page,
                                                                   @RequestParam @Nullable final Integer size,
                                                                   @RequestParam(required = false) final String query,
                                                                   @RequestParam(required = false) final Collection<String> location,
                                                                   @RequestParam(required = false) final Collection<String> orgTypes,
                                                                   @RequestParam(required = false) final Collection<String> categories,
                                                                   @RequestParam final Boolean showMap) {
        try {
            log.info("Starting to get organization");
            final Object pageResponse = modelDBService
                    .findOrganizationsForPublic(page,size,query,location,orgTypes,categories,showMap);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Organization fetched Successfully.").success(true)
                    .data(pageResponse)
                    .build();
            log.info("Organization fetched Successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Error occurred during operation";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
