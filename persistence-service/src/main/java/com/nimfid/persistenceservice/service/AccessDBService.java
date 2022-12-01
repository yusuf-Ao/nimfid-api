package com.nimfid.persistenceservice.service;

import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.enums.AccountStatus;
import com.nimfid.commons.exception.CustomException;
import com.nimfid.commons.exception.RefreshTokenException;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.JwtResponse;
import com.nimfid.commons.token.AuthenticationTokenDetails;
import com.nimfid.commons.token.AuthenticationTokenParser;
import com.nimfid.commons.token.AuthenticationTokenService;
import com.nimfid.commons.util.AdminConfig;
import com.nimfid.persistenceservice.data.TokenStore;
import com.nimfid.persistenceservice.repo.TokenRepository;
import com.nimfid.persistenceservice.repo.UserRepository;
import com.nimfid.persistenceservice.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccessDBService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final AuthenticationTokenService authenticationTokenService;
    @Autowired
    private final PasswordUtil passwordUtil;
    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private final AdminConfig adminConfig;
    @Autowired
    private AuthenticationTokenParser authenticationTokenParser;

    @Transactional
    public JwtResponse authenticateUser(final String email, final String loginPassword) {
        try {
            final Optional<UserStore> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                final String message = "Username does not exist";
                log.error(message);
                throw new CustomException(HttpStatus.UNAUTHORIZED, message);
            }
            if (user.get().getAccountStatus().equals(AccountStatus.UNVERIFIED)) {
                final String message = "Please complete user registration by verifying your email";
                log.error(message);
                throw new CustomException(HttpStatus.NOT_MODIFIED, message);
            }
            final String userPassword = user.get().getPassword().trim();
            final boolean isValidPassword = passwordUtil.passwordEncoder()
                    .matches(loginPassword, userPassword);
            if (isValidPassword) {
                final UserDetails userDetails = UserDetails.builder()
                        .id(user.get().getId())
                        .uuid(user.get().getUuid())
                        .firstName(user.get().getFirstName()).lastName(user.get().getLastName())
                        .otherNames(user.get().getOtherNames()).email(user.get().getEmail())
                        .userRoles(user.get().getUserRoles()).accountStatus(user.get().getAccountStatus())
                        .userStatus(user.get().getUserStatus())
                        .build();
                JwtResponse response = authenticationTokenService.issueToken(userDetails);
                tokenRepository.save(TokenStore.builder()
                        .token(response.getRefreshToken())
                        .uuid(user.get().getUuid())
                        .build());
                return response;
            }
            throw new CustomException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public JwtResponse authenticateSystemUser(final String username, final String loginPassword) {
        try {
            final String systemUsername = adminConfig.getUsername();
            final String systemPassword = adminConfig.getPassword();
            final String userName = username.trim();
            final String userPassword = loginPassword.trim();
            if (!userName.matches(systemUsername)) {
                final String message = "User does not exist";
                log.error(message);
                throw new CustomException(HttpStatus.UNAUTHORIZED, message);
            }

            final boolean isValidPassword = passwordUtil.passwordEncoder()
                    .matches(userPassword, systemPassword);
            if (isValidPassword) {
                JwtResponse response = authenticationTokenService.issueToken(adminConfig.getSystemUserDetails());
                tokenRepository.save(TokenStore.builder()
                        .token(response.getRefreshToken())
                        .uuid(adminConfig.getUuid())
                        .build());
                return response;
            }
            throw new CustomException(HttpStatus.UNAUTHORIZED, "username or password is incorrect");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }


    //TODO:store refresh token tokenId instead of the refresh token itself
    @Transactional
    public void revokeAccess(final String refreshToken, final UserDetails userDetailDto) throws CustomException {
        try {
            final AuthenticationTokenDetails authDetailsFromToken = authenticationTokenParser.parseRefreshToken(refreshToken);
            final String uuidFromToken = authDetailsFromToken.getUuid();
            final String uuidFromUserDetails = userDetailDto.getUuid();
            if (!uuidFromToken.matches(uuidFromUserDetails)) {
                final String message = "Unable to revoke token";
                log.error(message);
                throw new CustomException(HttpStatus.EXPECTATION_FAILED, message);
            }
            tokenRepository.deleteTokenFromStore(uuidFromUserDetails, refreshToken);
        } catch (final Exception e) {
            final String message = "Unable to revoke token";
            log.error(message);
            throw new CustomException(HttpStatus.EXPECTATION_FAILED, message);
        }
    }

    private boolean isTokenPresent(final String uuid, final String refreshToken) {
        return tokenRepository.existsByUuidAndToken(uuid, refreshToken);
    }

    public String refreshToken(final String refreshToken, final String currentAuthenticationToken)
            throws CustomException, RefreshTokenException {
        AuthenticationTokenDetails userDetails;
        try {
            userDetails = authenticationTokenParser.parseRefreshToken(refreshToken);
        } catch (final RefreshTokenException e) {
            if (tokenRepository.existsByToken(refreshToken)) {
                tokenRepository.deleteToken(refreshToken);
            }
            final String message = "Invalid refresh token...Please Login";
            log.error(message);
            throw new RefreshTokenException(HttpStatus.FORBIDDEN, message);
        }
        final String uuid = userDetails.getUuid();
        if (!isTokenPresent(uuid, refreshToken)) {
            final String message = "Invalid refresh token...Please Login";
            log.error(message);
            throw new RefreshTokenException(HttpStatus.FORBIDDEN, message);
        }
        return authenticationTokenService.refreshToken(refreshToken, currentAuthenticationToken);
    }
}
