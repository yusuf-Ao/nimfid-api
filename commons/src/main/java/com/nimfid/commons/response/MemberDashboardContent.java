package com.nimfid.commons.response;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class MemberDashboardContent {

    private int totalNumberOfOrganizations;
    private int numberOfActiveOrgs;
    private int numberOfInactiveOrgs;
}
