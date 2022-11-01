package com.nimfid.apigateway.service;

import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.util.AdminConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FraudCheck {

    @Autowired
    private final FraudCheckRepo fraudCheckRepo;
    @Autowired
    private final AdminConfig adminConfig;

    public Optional<UserDetails> verifyUserExistence(final Long id, final String uuid) {
        log.info("Attempting to verify user existence");
        Optional<UserStore> userModel = fraudCheckRepo.findByIdAndUuid(id, uuid);
        if (uuid.matches(adminConfig.getUuid())) {
            return Optional.ofNullable(adminConfig.getSystemUserDetails());
        }
        return Optional.of(UserDetails.builder()
                .id(userModel.get().getId())
                .uuid(userModel.get().getUuid())
                .firstName(userModel.get().getFirstName())
                .lastName(userModel.get().getLastName())
                .otherNames(userModel.get().getOtherNames())
                .email(userModel.get().getEmail())
                .accountStatus(userModel.get().getAccountStatus())
                .userStatus(userModel.get().getUserStatus())
                .userRoles(userModel.get().getUserRoles())
                .build());
    }
}
