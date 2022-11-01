package com.nimfid.commons.util;


import com.auth0.jwt.algorithms.Algorithm;
import com.nimfid.commons.token.AuthenticationTokenSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlgorithmUtil {
    @Autowired
    private final AuthenticationTokenSettings authenticationTokenSettings;

    public Algorithm getAlgorithmForAccessToken() {
        return Algorithm.HMAC512(authenticationTokenSettings.getSecretKey());
    }
    public Algorithm getAlgorithmForRefreshToken() {
        return Algorithm.HMAC256(authenticationTokenSettings.getSecretKey());
    }
}
