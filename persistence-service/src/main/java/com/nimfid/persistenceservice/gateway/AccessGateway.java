package com.nimfid.persistenceservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimfid.commons.request.LoginRequest;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.CustomResponse;
import com.nimfid.commons.response.JwtResponse;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.persistenceservice.service.AccessDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;
import java.util.Objects;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AccessGateway {
    @Autowired
    private  AccessDBService accessDBService;

    @PostMapping("/user-login")
    public ResponseEntity<?> userLogin(@RequestBody @Valid final LoginRequest loginRequest,
                                   @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempt user login");
            final JwtResponse jwtResponse = accessDBService.authenticateUser(loginRequest.getUsername(),
                    loginRequest.getPassword(), httpHeaders);
            log.info("user login success");
            return new ResponseEntity<>(jwtResponse, HttpStatus.ACCEPTED);
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

    @PostMapping("/system-login")
    public ResponseEntity<?> systemLogin(@RequestParam final String username,
                                       @RequestParam final String password,
                                       @RequestHeader HttpHeaders httpHeaders) {
        try {
            log.info("Attempt system login");
            final JwtResponse jwtResponse = accessDBService.authenticateSystemUser(username,
                    password, httpHeaders);
            log.info("system login success");
            return new ResponseEntity<>(jwtResponse, HttpStatus.ACCEPTED);
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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@HeaderParam("Authorization") final String authorizationToken,
                                          @RequestHeader HttpHeaders httpHeaders,
                                          @RequestBody JwtResponse jwtResponse) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Attempting to issue new access token");
            final String      currentAuthenticationToken = authorizationToken.replaceFirst("Bearer", "");
            final String      newToken                 = accessDBService.refreshToken(jwtResponse.getRefreshToken(),
                    currentAuthenticationToken, authUserDetails);
            final JwtResponse response = JwtResponse.builder().accessToken(newToken).build();
            log.info("New access token issued");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (final Exception e) {
            final String message = "Token not yet expired";
            log.error(message,e);
            CustomResponse response = CustomResponse.builder().timeStamp(TimeUtil.getFormattedDateTimeOfInstant())
                    .statusCode(HttpStatus.EXPECTATION_FAILED.value()).status(HttpStatus.EXPECTATION_FAILED)
                    .message(message).reason(e.getMessage()).success(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    //todo: adjust logout mechanism... only issuer access token should be able to logout
    // TODO: logged in user should only logout his session..
    @PostMapping("/revoke-access")
    public ResponseEntity<?> revokeAccess(@RequestHeader HttpHeaders httpHeaders,
                                          @RequestBody JwtResponse jwtResponse) {
        try {
            log.info("Attempting to extract user details from request header");
            final UserDetails authUserDetails = extractUserDetailsFromHeader(httpHeaders);
            log.info("Attempting to revoke token access");
            accessDBService.revokeAccess(jwtResponse.getRefreshToken(), authUserDetails);
            log.info("Token revoked successfully");
            return new ResponseEntity<>("logout success", HttpStatus.OK);
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
