package com.nimfid.commons.token;


import com.auth0.jwt.JWT;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.util.AlgorithmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AuthenticationTokenIssuer {

    @Autowired
    private final AlgorithmUtil algorithmUtil;
    @Autowired
    private  AuthenticationTokenSettings authenticationTokenSettings;
    public String issueAccessToken(final AuthenticationTokenDetails authenticationTokenDetails) {
        return JWT.create()
                .withJWTId(authenticationTokenDetails.getTokenId())
                .withIssuer(authenticationTokenSettings.getIssuer())
                .withAudience(authenticationTokenSettings.getAudience())
                .withSubject(String.valueOf(authenticationTokenDetails.getUserId()))
                .withIssuedAt(Date.from(authenticationTokenDetails.getIssuedDate().toInstant()))
                .withExpiresAt(Date.from(authenticationTokenDetails.getExpirationDate().toInstant()))
                .withClaim(authenticationTokenSettings.getUserIdClaimName(), authenticationTokenDetails.getUserId())
                .withClaim(authenticationTokenSettings.getUuidClaimName(), authenticationTokenDetails.getUuid())
                .withClaim(authenticationTokenSettings.getAuthorityClaimName(), authenticationTokenDetails.getUserRoles().stream()
                        .map(UserRoles::getUserRole)
                        .collect(Collectors.toList()))
                .withClaim(authenticationTokenSettings.getSubjectClaimName(), AuthenticationTokenSettings.ALIAS)
                .sign(algorithmUtil.getAlgorithmForAccessToken());

    }

    public String issueRefreshToken(final AuthenticationTokenDetails authenticationTokenDetails) {
        return JWT.create()
                .withJWTId(authenticationTokenDetails.getTokenId())
                .withIssuer(authenticationTokenSettings.getIssuer())
                .withAudience(authenticationTokenSettings.getAudience())
                .withIssuedAt(Date.from(authenticationTokenDetails.getIssuedDate().toInstant()))
                .withExpiresAt(Date.from(authenticationTokenDetails.getExpirationDate().toInstant()))
                .withClaim(authenticationTokenSettings.getUserIdClaimName(), authenticationTokenDetails.getUserId())
                .withClaim(authenticationTokenSettings.getUuidClaimName(), authenticationTokenDetails.getUuid())
                .sign(algorithmUtil.getAlgorithmForRefreshToken());
    }
}
