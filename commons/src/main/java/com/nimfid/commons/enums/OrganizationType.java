package com.nimfid.commons.enums;

public enum OrganizationType {

    COOPERATIVE("COOPERATIVE"),
    MFI("MFI"),
    NGO("NGO");

    private final String organizationType;

    OrganizationType(final String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationType() {
        return organizationType;
    }
}
