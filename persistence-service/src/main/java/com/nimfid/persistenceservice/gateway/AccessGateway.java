package com.nimfid.persistenceservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimfid.commons.exception.RefreshTokenException;
import com.nimfid.commons.request.LoginRequest;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.response.JwtResponse;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.persistenceservice.service.AccessDBService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AccessGateway {
    @Autowired
    private  AccessDBService accessDBService;

    @Operation(summary = "User Login", description = "To be used for authenticating member user(Normal Users)")
    @PostMapping("/user-login")
    public ResponseEntity<CustomResponse> userLogin(@RequestBody final LoginRequest loginRequest) {
        try {
            log.info("Attempt user login");
            final JwtResponse jwtResponse = accessDBService.authenticateUser(loginRequest.getUsername(),
                    loginRequest.getPassword());
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.ACCEPTED.value()).status(HttpStatus.ACCEPTED)
                    .message("user login success").success(true)
                    .data(jwtResponse)
                    .build();
            log.info("user login success");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (final Exception e) {
            final String message = "Login Failed";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.UNAUTHORIZED.value()).status(HttpStatus.UNAUTHORIZED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "System Login", description = "To be used for authenticating system user(Admin)")
    @PostMapping("/system-login")
    public ResponseEntity<CustomResponse> systemLogin(@RequestBody final LoginRequest loginRequest) {
        try {
            log.info("Attempt system login");
            final JwtResponse jwtResponse = accessDBService.authenticateSystemUser(loginRequest.getUsername(),
                    loginRequest.getPassword());
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.ACCEPTED.value()).status(HttpStatus.ACCEPTED)
                    .message("system login success").success(true)
                    .data(jwtResponse)
                    .build();
            log.info("system login success");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (final Exception e) {
            final String message = "Login Failed";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.UNAUTHORIZED.value()).status(HttpStatus.UNAUTHORIZED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Refresh Token", description = "To be used for issuing new access token",
            security = { @SecurityRequirement(name = "Bearer Token") })
    @PostMapping("/refresh-token")
    public ResponseEntity<CustomResponse> refreshToken(@RequestHeader HttpHeaders httpHeaders,
                                          @RequestBody JwtResponse jwtResponse) {
        try {
            log.info("Attempting to issue new access token");
            final String      authorizationToken = httpHeaders.get(HttpHeaders.AUTHORIZATION).get(0);
            final String      currentAuthenticationToken = authorizationToken.replaceFirst("Bearer", "").trim();
            final String      newToken                 = accessDBService.refreshToken(jwtResponse.getRefreshToken(),
                    currentAuthenticationToken);
            final JwtResponse refreshResponse = JwtResponse.builder().accessToken(newToken).build();
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("New access token issued").success(true)
                    .data(refreshResponse)
                    .build();
            log.info("New access token issued");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Invalid Refresh Token";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.UNAUTHORIZED.value()).status(HttpStatus.UNAUTHORIZED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    //todo: adjust logout mechanism... only issuer access token should be able to logout
    // TODO: logged in user should only logout his session..
    @Operation(summary = "Logout", description = "To be used for revoking token access.",
            security = { @SecurityRequirement(name = "Bearer Token") })
    @PostMapping("/revoke-access")
    public ResponseEntity<CustomResponse> revokeAccess(@RequestHeader HttpHeaders httpHeaders,
                                          @RequestBody JwtResponse jwtResponse) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Attempting to revoke token access");
            accessDBService.revokeAccess(jwtResponse.getRefreshToken(), authUserDetails);
            log.info("Token revoked successfully");
            final CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message("logout success").success(true)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "No active session found";
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
