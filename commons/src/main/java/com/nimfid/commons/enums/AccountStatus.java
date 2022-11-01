package com.nimfid.commons.enums;


public enum AccountStatus {

    VERIFIED("VERIFIED"),
    UNVERIFIED("UNVERIFIED");

    private final String accountStatus;

    AccountStatus(final String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
