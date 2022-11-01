package com.nimfid.commons.enums;

public enum UserRoles {

    MEMBER_USER("MEMBER_USER"),
    SYSTEM_USER("SYSTEM_USER");

    private final String userRole;

    UserRoles(final String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }
}
