package com.nimfid.commons.token;


import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.exception.AccessTokenException;
import com.nimfid.commons.exception.CustomException;
import com.nimfid.commons.exception.RefreshTokenException;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.JwtResponse;
import com.nimfid.commons.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationTokenService {

    @Autowired
    private final AuthenticationTokenParser       authenticationTokenParser;
    @Autowired
    private final AuthenticationTokenSettings     authenticationTokenSettings;
    @Autowired
    private final AuthenticationTokenIssuer       authenticationTokenIssuer;
    private AuthenticationTokenDetails            refreshAuthenticationDetails;
    private String                          tokenId;
    private Long                            userId;
    private String                          uuid;
    private Collection<UserRoles>           userRoles;
    private ZonedDateTime                   issuedDate;



    private ZonedDateTime calculateAccessExpirationDate(final ZonedDateTime issuedDate) {
        return issuedDate.plusSeconds(authenticationTokenSettings.getAccessTokenExpiration());
    }

    private ZonedDateTime calculateRefreshExpirationDate(final ZonedDateTime issuedDate) {
        return issuedDate.plusSeconds(authenticationTokenSettings.getRefreshTokenExpiration());
    }

    private String generateTokenIdentifier() {
        return UUID.randomUUID().toString();
    }

    public JwtResponse issueToken(final UserDetails userDetails) {

        tokenId     = generateTokenIdentifier();
        userId      = userDetails.getId();
        uuid        = userDetails.getUuid();
        userRoles   = userDetails.getUserRoles();
        issuedDate  = TimeUtil.getZonedDateTimeOfInstant();

        final ZonedDateTime accessExpirationDate  = calculateAccessExpirationDate(issuedDate);
        final ZonedDateTime refreshExpirationDate = calculateRefreshExpirationDate(issuedDate);

        return JwtResponse.builder()
                .refreshToken(authenticationTokenIssuer.issueRefreshToken(
                        AuthenticationTokenDetails.builder().tokenId(tokenId)
                                .userId(userId).uuid(uuid)
                                .userRoles(userRoles).issuedDate(issuedDate)
                                .expirationDate(refreshExpirationDate).build()))
                .accessToken(authenticationTokenIssuer.issueAccessToken(
                        AuthenticationTokenDetails.builder().tokenId(tokenId)
                                .userId(userId).uuid(uuid)
                                .userRoles(userRoles).issuedDate(issuedDate)
                                .expirationDate(accessExpirationDate).build()))
                .build();

    }

    public AuthenticationTokenDetails parseAccessToken(final String authenticationToken) throws AccessTokenException {
        return authenticationTokenParser.parseAccessToken(authenticationToken);
    }

    public AuthenticationTokenDetails parseRefreshToken(final String authenticationToken) throws RefreshTokenException {
        return authenticationTokenParser.parseRefreshToken(authenticationToken);
    }

    public String refreshToken(final String refreshToken, final String currentAuthenticationToken) throws CustomException {
        issuedDate = ZonedDateTime.now();
        final ZonedDateTime expirationDate = calculateAccessExpirationDate(issuedDate);
        boolean             tokenExpired   = false;

        try {
            parseAccessToken(currentAuthenticationToken);
        } catch(final AccessTokenException e) {
            tokenExpired = true;
        }

        if(!tokenExpired) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE,"Token has not expired.");
        }
        try {
            refreshAuthenticationDetails = parseRefreshToken(refreshToken);
        } catch(final RefreshTokenException e) {
            throw new CustomException(HttpStatus.FORBIDDEN,"Refresh token expired.");
        }

        userId = refreshAuthenticationDetails.getUserId();
        uuid = refreshAuthenticationDetails.getUuid();
        tokenId  = refreshAuthenticationDetails.getTokenId();
        userRoles = refreshAuthenticationDetails.getUserRoles();
        final AuthenticationTokenDetails newTokenDetails = AuthenticationTokenDetails.builder()
                .tokenId(tokenId).userId(userId)
                .issuedDate(issuedDate)
                .expirationDate(expirationDate)
                .uuid(uuid)
                .userRoles(userRoles).build();

        return authenticationTokenIssuer.issueAccessToken(newTokenDetails);
    }
}
