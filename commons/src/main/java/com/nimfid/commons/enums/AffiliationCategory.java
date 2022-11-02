package com.nimfid.commons.enums;

public enum AffiliationCategory {

    A("A"), B("B"), C("C"), D("D"), E("E");

    private final String affiliationCategory;

    AffiliationCategory(final String affiliationCategory) {
        this.affiliationCategory = affiliationCategory;
    }

    public String getAffiliationCategory() {
        return affiliationCategory;
    }
}
