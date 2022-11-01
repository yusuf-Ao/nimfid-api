package com.nimfid.commons.enums;

public enum UserStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    RESTRICTED("RESTRICTED");

    private final String userStatus;

    UserStatus(final String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }
}
