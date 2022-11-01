package com.nimfid.persistenceservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.request.*;
import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.persistenceservice.service.UserDBService;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserGateway {
    @Autowired
    private final UserDBService userDBService;

    @GetMapping("/health-check")
    public ResponseEntity<CustomResponse> healthCheck() {
        CustomResponse response = CustomResponse.builder()
                .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                .timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                .message("Hello").success(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CustomResponse> registerAccount(@RequestBody @Valid final UserCreationDto userCreationDto) {
        try {
            log.info("Starting to add user");
            final UserStore user = userDBService.registerUser(userCreationDto);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.CREATED.value()).status(HttpStatus.CREATED)
                    .message("User Added Successfully.. Please proceed to mail verification").success(true)
                    .data(user)
                    .build();
            log.info("User Added Successfully.. Please proceed to mail verification");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (final Exception e) {
            final String message = "Unable to add user to the database";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<CustomResponse> verifyAccount(@NotNull @RequestParam("email") final String email,
                                                        @NotNull @RequestParam("code") final String code) {
        try {
            log.info("Starting to verify user email");
            userDBService.verifyUserEmail(email, code);
            final CustomResponse response =  CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User Email verified Successfully").success(true)
                    .build();
            log.info("User Email verified Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to verify email";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/email-availability")
    public ResponseEntity<CustomResponse> checkEmailAvailability(@NotNull @RequestParam("email") final String email) {
        try {
            log.info("Starting to verify email");
            boolean emailExists =  userDBService.userExistsByEmail(email);
            final String message = emailExists? "Email is already in use" : "Email is available";
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message(message).success(true)
                    .data(emailExists)
                    .build();
            log.info("User Email verified Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to verify email";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<CustomResponse> resendVerificationCode(@NotNull @RequestParam("email") final String email) {
        try {
            log.info("Starting to verify email");
            userDBService.resendVerificationCode(email);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("Verification code sent successfully").success(true)
                    .build();
            log.info("Verification code sent successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to verify email";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping
    public ResponseEntity<CustomResponse> getUserProfile(@RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Trying to get user");
            final UserStore user = userDBService.getUserProfile(authUserDetails);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User Profile Retrieved Successfully").success(true)
                    .data(user)
                    .build();
            log.info("User Profile Retrieved Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e){
            final String message = "User is not present in the database";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<CustomResponse> updateUserProfile(@RequestBody final UserUpdateDto userUpdateDto,
                                                            @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Trying to update user");
            final UserStore user = userDBService.updateUserInfo(userUpdateDto, authUserDetails);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User Updated Successfully").success(true)
                    .data(user)
                    .build();
            log.info("User Updated Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to update user";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<CustomResponse> updateUserPassword(@RequestBody @Valid final PasswordUpdateDto passwordUpdateDto,
                                                             @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Trying to update user password");
            userDBService.updateUserPassword(passwordUpdateDto, authUserDetails);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User password Updated Successfully").success(true)
                    .build();
            log.info("User password Updated Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to update user password";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CustomResponse> forgotPassword(@RequestParam final String email) {
        try {
            log.info("Trying to reset user password");
            userDBService.findAccountAndSendOTPForPasswordReset(email);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("OTP for password reset has been sent to email").success(true)
                    .build();
            log.info("OTP for password reset has been sent to email");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to reset user password";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomResponse> resetPassword(@RequestBody @Valid final ForgotPasswordRequest forgotPasswordRequest,
                                                        @RequestParam final String email) {
        try {
            log.info("Trying to reset user password");
            userDBService.resetPasswordWithOTP(forgotPasswordRequest, email);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User password reset Successfully").success(true)
                    .build();
            log.info("User password reset Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to reset user password";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /*@DeleteMapping("/delete")
    public ResponseEntity<CustomResponse> deleteUserAccount(@RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Trying to delete user");
            userDBService.deleteUserAccount(authUserDetails);
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("User Deleted Successfully").success(true)
                    .build();
            log.info("User Deleted Successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Unable to delete user account";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }*/

    private UserDetails extractUserDetailsFromHeader(final HttpHeaders httpHeaders) throws JsonProcessingException {
        String authenticatedUser = Objects.requireNonNull(httpHeaders.get("authenticatedUser")).get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(authenticatedUser, UserDetails.class);
    }
}
