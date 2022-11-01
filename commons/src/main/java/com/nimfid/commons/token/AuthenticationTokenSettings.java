package com.nimfid.commons.token;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AuthenticationTokenSettings {

    protected static final String ALIAS    = "spring";

    @Value("${security.jwt.issuer}")
    private  String      issuer;
    @Value("${security.jwt.audience}")
    private  String     audience;
    @Value("${security.jwt.secret-key}")
    private  String     secretKey;
    @Value("${security.jwt.refresh-token.validFor-seconds}")
    private  Long      refreshTokenExpiration;
    @Value("${security.jwt.access-token.validFor-seconds}")
    private  Long      accessTokenExpiration;
    @Value("${security.jwt.claim-names.authority}")
    private  String      authorityClaimName;
    @Value("${security.jwt.claim-names.userId}")
    private  String      userIdClaimName;
    @Value("${security.jwt.claim-names.uuid}")
    private  String     uuidClaimName;
    @Value("${security.jwt.claim-names.subject}")
    private  String      subjectClaimName;


    public  String getSecretKey(){
        //todo: to make this external and encrypt
        return secretKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public  String getAudience() {
        return audience;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public  Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public  String getAuthorityClaimName() {
        return authorityClaimName;
    }

    public  String getUserIdClaimName() {
        return userIdClaimName;
    }

    public  String getSubjectClaimName() {
        return subjectClaimName;
    }

    public  String getUuidClaimName() {
        return uuidClaimName;
    }

}
