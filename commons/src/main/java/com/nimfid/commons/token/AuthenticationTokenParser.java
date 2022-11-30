package com.nimfid.commons.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.exception.AccessTokenException;
import com.nimfid.commons.exception.RefreshTokenException;
import com.nimfid.commons.util.AlgorithmUtil;
import com.nimfid.commons.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationTokenParser {
    @Autowired
    private final AlgorithmUtil algorithmUtil;
    @Autowired
    private final AuthenticationTokenSettings authenticationTokenSettings;

    public AuthenticationTokenDetails parseAccessToken(final String token) throws AccessTokenException {
        try {
            Algorithm   algorithm       = algorithmUtil.getAlgorithmForAccessToken();
            JWTVerifier verifier        = JWT.require(algorithm)
                    .withAudience(authenticationTokenSettings.getAudience()).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String[] roles = decodedJWT.getClaim(authenticationTokenSettings.getAuthorityClaimName()).asArray(String.class);
            Collection<UserRoles> authorities = new ArrayList<>();
            stream(roles).forEach(role -> {
                authorities.add(UserRoles.valueOf(role));
            });
            return AuthenticationTokenDetails.builder()
                    .tokenId(decodedJWT.getId())
                    .userId(decodedJWT.getClaim(authenticationTokenSettings.getUserIdClaimName()).asLong())
                    .uuid(decodedJWT.getClaim(authenticationTokenSettings.getUuidClaimName()).asString())
                    .issuedDate(ZonedDateTime.ofInstant(decodedJWT.getIssuedAtAsInstant(), TimeUtil.getZONE_ID()))
                    .expirationDate(ZonedDateTime.ofInstant(decodedJWT.getExpiresAtAsInstant(), TimeUtil.getZONE_ID()))
                    .userRoles(authorities)
                    .build();
        } catch (final Exception e) {
            final String message = "Invalid access token";
            log.error(message,e);
            throw new AccessTokenException(HttpStatus.FORBIDDEN, message, e);
        }
    }

    public AuthenticationTokenDetails parseRefreshToken(final String refreshToken) throws RefreshTokenException {
        try {
            Algorithm   algorithm       = algorithmUtil.getAlgorithmForRefreshToken();
            JWTVerifier verifier        = JWT.require(algorithm)
                    .withAudience(authenticationTokenSettings.getAudience()).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String[] roles = decodedJWT.getClaim(authenticationTokenSettings.getAuthorityClaimName()).asArray(String.class);
            Collection<UserRoles> authorities = new ArrayList<>();
            stream(roles).forEach(role -> {
                authorities.add(UserRoles.valueOf(role));
            });
            return AuthenticationTokenDetails.builder()
                    .tokenId(decodedJWT.getId())
                    .userId(decodedJWT.getClaim(authenticationTokenSettings.getUserIdClaimName()).asLong())
                    .uuid(decodedJWT.getClaim(authenticationTokenSettings.getUuidClaimName()).asString())
                    .issuedDate(ZonedDateTime.ofInstant(decodedJWT.getIssuedAtAsInstant(), TimeUtil.getZONE_ID()))
                    .expirationDate(ZonedDateTime.ofInstant(decodedJWT.getExpiresAtAsInstant(), TimeUtil.getZONE_ID()))
                    .userRoles(authorities)
                    .build();
        } catch (final Exception e) {
            final String message = "Invalid refresh token";
            log.error(message,e);
            throw new RefreshTokenException(HttpStatus.FORBIDDEN, message, e);
        }
    }
}
