package com.nimfid.commons.request;

import com.nimfid.commons.enums.AffiliationCategory;
import com.nimfid.commons.enums.OrganizationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class OrgUpdateDto {


    private OrganizationType        organizationType;
    private Long                    capitalOwned;
    private String                  officeAddress;
    private String                  village_ward;
    private String                  state;
    private String                  lga;
    private String                  city;
    private String                  officialEmail;
    private Long                    officePhone;
    private String                  website;
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
