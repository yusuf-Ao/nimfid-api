package com.nimfid.commons.request;

import com.google.gson.Gson;
import com.nimfid.commons.enums.AccountStatus;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.enums.UserStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class UserDetails {

    private Long                    id;
    private String                  uuid;
    private String                  firstName;
    private String                  lastName;
    private String                  otherNames;
    private String                  email;
    private Collection<UserRoles>   userRoles;
    private AccountStatus           accountStatus;
    private UserStatus              userStatus;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, UserDetails.class);
    }
}
