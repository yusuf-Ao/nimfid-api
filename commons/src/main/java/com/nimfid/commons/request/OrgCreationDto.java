package com.nimfid.commons.request;


import com.nimfid.commons.enums.AffiliationCategory;
import com.nimfid.commons.enums.OrganizationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class OrgCreationDto {
    @NotEmpty
    @NotNull
    private String                  orgName;
    private OrganizationType        organizationType;
    @NotEmpty
    @NotNull
    private String                  officeAddress;
    private String                  village_ward;
    @NotEmpty
    @NotNull
    private String                  state;
    @NotEmpty
    @NotNull
    private String                  lga;
    private String                  city;
    @NotEmpty
    @NotNull
    private String                  officialEmail;
    @NotNull
    private Long                    officePhone;
    private String                  website;
    private Long                    capitalOwned;
    private Long                    menMembers;
    private Long                    womenMembers;
    private Long                    physicallyDisabledMembers;
    private Long                    youthMembers;
    private Long                    totalMembers;
    private Long                    menLoan;
    private Long                    womenLoan;
    private Long                    physicallyDisabledLoan;
    private Long                    youthLoan;
    private Long                    totalLoan;
    private Long                    menSavings;
    private Long                    womenSavings;
    private Long                    physicallyDisabledSavings;
    private Long                    youthSavings;
    private Long                    totalSavings;
    private String                  affiliationNumber;
    private AffiliationCategory     affiliationCategory;

}
