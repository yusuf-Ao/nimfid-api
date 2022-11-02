package com.nimfid.commons.enums;

public enum OrganizationStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    RESTRICTED("RESTRICTED");

    private final String organizationStatus;

    OrganizationStatus(final String organizationStatus) {
        this.organizationStatus = organizationStatus;
    }

    public String getOrganizationStatus() {
        return organizationStatus;
    }
}
