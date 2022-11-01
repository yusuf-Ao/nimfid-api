package com.nimfid.commons.token;

import com.nimfid.commons.enums.UserRoles;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.Collection;

@SuperBuilder
@Data
@RequiredArgsConstructor
public class AuthenticationTokenDetails {

    private final String                    tokenId;
    private final Long                      userId;
    private final String                    uuid;
    private final ZonedDateTime             issuedDate;
    private final ZonedDateTime             expirationDate;
    private final Collection<UserRoles>     userRoles;
}
