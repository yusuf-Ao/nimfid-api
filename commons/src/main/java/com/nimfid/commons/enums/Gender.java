package com.nimfid.commons.enums;

public enum Gender {

    MALE("MALE"),
    FEMALE("FEMALE");

    private final String gender;

    Gender(final String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
}
