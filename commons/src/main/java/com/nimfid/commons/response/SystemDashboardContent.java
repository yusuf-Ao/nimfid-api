package com.nimfid.commons.response;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class SystemDashboardContent {

    private int totalNumberOfOrganizations;
    private int numberOfActiveOrgs;
    private int numberOfInactiveOrgs;

    private int totalNumberOfUsers;
    private int numberOfActiveUsers;
    private int numberOfInactiveUsers;

}
