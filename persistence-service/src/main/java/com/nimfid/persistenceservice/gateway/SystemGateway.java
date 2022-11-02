package com.nimfid.persistenceservice.gateway;


import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.response.SystemDashboardContent;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.persistenceservice.service.SystemService;
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
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Slf4j
public class SystemGateway {

    @Autowired
    private final SystemService systemService;


    @GetMapping("/fetch-users")
    public ResponseEntity<CustomResponse> getAllUsers(@RequestParam final int page, @RequestParam final int size) {
        try {
            log.info("Trying to fetch all users");
            final PageResponse userPageResponse = systemService.getAllUsers(page, size);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Users fetched Successfully").success(true)
                    .data(userPageResponse)
                    .build();
            log.info("Users fetched Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to fetch users";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/partial-response-stats")
    public ResponseEntity<SystemDashboardContent> getStatsPartial() {
        try {
            log.info("Trying to fetch Partial stats");
            final SystemDashboardContent systemDashboardContent = systemService.getPartialContent();
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Partial stats fetched Successfully").success(true)
                    .data(systemDashboardContent)
                    .build();
            log.info("Partial stats fetched Successfully");
            return new ResponseEntity<>(systemDashboardContent, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to fetch content";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /*@GetMapping("/fetch-user/{userId}")
    public ResponseEntity<CustomResponse> getUser(@NotNull @PathVariable("userId") final Long userId) {
        try {
            log.info("Trying to fetch  user details");
            final UserStore user = systemService.getUser(userId);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User fetched Successfully").success(true)
                    .data(user)
                    .build();
            log.info("User fetched Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to fetch user";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }*/
}
