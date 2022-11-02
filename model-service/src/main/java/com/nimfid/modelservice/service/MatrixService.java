package com.nimfid.modelservice.service;


import com.nimfid.commons.clients.persistenceservice.SystemGatewayClient;
import com.nimfid.commons.enums.OrganizationStatus;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.response.SystemDashboardContent;
import com.nimfid.modelservice.data.OrganizationModel;
import com.nimfid.modelservice.repo.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MatrixService {

    @Autowired
    private final OrganizationRepository organizationRepository;

    private final SystemGatewayClient systemGatewayClient;


    public PageResponse getAllOrgs(final int page, final int size) {
        log.info("Fetching all organizations");
        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationModel> orgsPage = organizationRepository.findAll(pageable);
        List<OrganizationModel> orgs = orgsPage.getContent();
        return PageResponse.builder()
                .pageContent(orgs)
                .currentPage(orgsPage.getNumber())
                .totalItems(orgsPage.getTotalElements())
                .totalPages(orgsPage.getTotalPages())
                .build();
    }

    public SystemDashboardContent getDashboardStats() {

        SystemDashboardContent dashboardContent = systemGatewayClient.getStatsPartial().getBody();

        assert dashboardContent != null;
        return  SystemDashboardContent.builder()
                .totalNumberOfOrganizations(getTotalOrgs())
                .numberOfActiveOrgs(getTotalOrgsByStatus(OrganizationStatus.ACTIVE))
                .numberOfInactiveOrgs(getTotalOrgsByStatus(OrganizationStatus.INACTIVE))
                .totalNumberOfUsers(dashboardContent.getTotalNumberOfUsers())
                .numberOfActiveUsers(dashboardContent.getNumberOfActiveUsers())
                .numberOfInactiveUsers(dashboardContent.getNumberOfInactiveUsers())
                .build();
    }

    private int getTotalOrgsByStatus(final OrganizationStatus status) {
        Integer result = organizationRepository.findTotalOrgsStatus(status.getOrganizationStatus());
        return result == null ? 0 : result;
    }

    private int getTotalOrgs() {
        Integer result = organizationRepository.findTotalOrgs();
        return result == null ? 0 : result;
    }



}
