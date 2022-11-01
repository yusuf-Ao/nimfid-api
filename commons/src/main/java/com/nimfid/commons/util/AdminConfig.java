package com.nimfid.commons.util;


import com.nimfid.commons.enums.AccountStatus;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.enums.UserStatus;
import com.nimfid.commons.request.UserDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Slf4j
@Configuration
@Getter
public class AdminConfig {

    @Value("${system-user.username}")
    private  String     username;
    @Value("${system-user.password}")
    private  String      password;
    @Value("${system-user.role}")
    private  String      role;
    @Value("${system-user.id}")
    private  Long      id;
    @Value("${system-user.uuid}")
    private  String      uuid;
    @Value("${system-user.firstName}")
    private  String      firstName;
    @Value("${system-user.lastName}")
    private  String      lastName;
    @Value("${system-user.otherNames}")
    private  String      otherNames;
    @Value("${system-user.accountStatus}")
    private  String      accountStatus;
    @Value("${system-user.userStatus}")
    private  String      userStatus;
    @Value("${system-user.email}")
    private  String      email;

    public UserRoles getRoleEnum() {
        return UserRoles.SYSTEM_USER;
    }

    public AccountStatus getAccountStatusEnum() {
        return AccountStatus.VERIFIED;
    }

    public UserStatus getUserStatusEnum() {
        return UserStatus.ACTIVE;
    }

    public UserDetails getSystemUserDetails() {
        return UserDetails.builder()
                .id(id)
                .uuid(uuid)
                .firstName(firstName).lastName(lastName)
                .otherNames(otherNames).email(email)
                .userRoles(Collections.singleton(getRoleEnum()))
                .accountStatus(getAccountStatusEnum())
                .userStatus(getUserStatusEnum())
                .build();
    }
}

