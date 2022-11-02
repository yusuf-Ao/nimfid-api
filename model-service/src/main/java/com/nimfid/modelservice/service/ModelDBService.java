package com.nimfid.modelservice.service;

import com.nimfid.commons.enums.OrganizationStatus;
import com.nimfid.commons.exception.CustomException;
import com.nimfid.commons.request.OrgCreationDto;
import com.nimfid.commons.request.OrgUpdateDto;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.MemberDashboardContent;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.util.CopyUtils;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.modelservice.data.*;
import com.nimfid.modelservice.repo.*;
import com.nimfid.modelservice.util.OrgUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ModelDBService {

    @Autowired
    private final OrganizationRepository organizationRepository;
    @Autowired
    private final ContactDetailsRepository contactDetailsRepository;
    @Autowired
    private final MembersRepository membersRepository;
    @Autowired
    private final AffiliationDetailsRepository affiliationDetailsRepository;
    @Autowired
    private final OutstandingPortfolioRepository portfolioRepository;
    @Autowired
    private final SavingsMobilizedRepository savingsMobilizedRepository;
    private final OrgUIDUtil orgUIDUtil;

    @Transactional
    public OrganizationModel addOrganization(final OrgCreationDto orgCreationDto, final UserDetails userDetails) throws CustomException {
        final String userUID = userDetails.getUuid();
        final String orgUid = orgUIDUtil.assignUuid();

        ZonedDateTime dateTime = TimeUtil.getZonedDateTimeOfInstant();

        String orgName = orgCreationDto.getOrgName().trim();

        if (organizationRepository.existsByOrgName(orgName)) {
            final String message = "Organization name already exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);

        }

        final ContactDetails contactDetails = ContactDetails.builder()
                        .officeAddress(orgCreationDto.getOfficeAddress())
                        .village_ward(orgCreationDto.getVillage_ward())
                        .state(orgCreationDto.getState()).lga(orgCreationDto.getLga())
                        .city(orgCreationDto.getCity())
                        .officialEmail(orgCreationDto.getOfficialEmail())
                        .officePhone(orgCreationDto.getOfficePhone())
                        .website(orgCreationDto.getWebsite())
                        .build();

        final Members members = Members.builder()
                .menMembers(orgCreationDto.getMenMembers())
                .womenMembers(orgCreationDto.getWomenMembers())
                .physicallyDisabledMembers(orgCreationDto.getPhysicallyDisabledMembers())
                .youthMembers(orgCreationDto.getYouthMembers())
                .totalMembers(orgCreationDto.getTotalMembers())
                .build();

        final OutstandingPortfolio outstandingPortfolio = OutstandingPortfolio.builder()
                .menLoan(orgCreationDto.getMenLoan())
                .womenLoan(orgCreationDto.getWomenLoan())
                .youthLoan(orgCreationDto.getYouthLoan())
                .physicallyDisabledLoan(orgCreationDto.getPhysicallyDisabledLoan())
                .totalLoan(orgCreationDto.getTotalLoan())
                .build();

        final SavingsMobilized savingsMobilized = SavingsMobilized.builder()
                .menSavings(orgCreationDto.getMenSavings())
                .womenSavings(orgCreationDto.getWomenSavings())
                .physicallyDisabledSavings(orgCreationDto.getPhysicallyDisabledSavings())
                .youthSavings(orgCreationDto.getYouthSavings())
                .totalSavings(orgCreationDto.getTotalSavings())
                .build();

        final AffiliationDetails affiliationDetails = AffiliationDetails.builder()
                .affiliationNumber(orgCreationDto.getAffiliationNumber())
                .affiliationCategory(orgCreationDto.getAffiliationCategory())
                .build();

        final OrganizationModel organizationModel = OrganizationModel.builder()
                .orgUID(orgUid)
                .orgName(orgName)
                .organizationType(orgCreationDto.getOrganizationType())
                .organizationStatus(OrganizationStatus.ACTIVE)
                .dateRegistered(dateTime)
                .lastModified(dateTime)
                .authorizedRepresentativeId(userUID)
                .capitalOwned(orgCreationDto.getCapitalOwned())
                .contactDetails(contactDetails)
                .affiliationDetails(affiliationDetails)
                .outstandingPortfolio(outstandingPortfolio)
                .members(members)
                .savingsMobilized(savingsMobilized)
                .build();
        contactDetailsRepository.save(contactDetails);
        membersRepository.save(members);
        affiliationDetailsRepository.save(affiliationDetails);
        portfolioRepository.save(outstandingPortfolio);
        savingsMobilizedRepository.save(savingsMobilized);
        organizationRepository.save(organizationModel);
        return organizationModel;
    }

    public PageResponse fetchOrganizations(final int page, final int size, final UserDetails userDetails) {
        final String userUID = userDetails.getUuid();
        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationModel> orgPage = organizationRepository.findAllByAuthorizedRepresentativeId(userUID, pageable);
        List<OrganizationModel> orgs = orgPage.getContent();
        return PageResponse.builder()
                .pageContent(orgs)
                .currentPage(orgPage.getNumber())
                .totalItems(orgPage.getTotalElements())
                .totalPages(orgPage.getTotalPages())
                .build();
    }

    @Transactional
    public void deleteOrg(final Long orgId, final UserDetails userDetails) throws CustomException {
        log.info("Deleting org with id: {} ", orgId);
        final String userUID = userDetails.getUuid();
        Optional<OrganizationModel> organizationModel = getOrgByIdAndUserUID(orgId, userUID);
        if (organizationModel.isEmpty()) {
            final String message = "Org with id: " + orgId + " does not exist";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        try {

            affiliationDetailsRepository.deleteById(organizationModel.get().getAffiliationDetails().getId());
            contactDetailsRepository.deleteById(organizationModel.get().getContactDetails().getId());
            membersRepository.deleteById(organizationModel.get().getMembers().getId());
            portfolioRepository.deleteById(organizationModel.get().getOutstandingPortfolio().getId());
            savingsMobilizedRepository.deleteById(organizationModel.get().getSavingsMobilized().getId());

            organizationRepository.deleteById(orgId);
        } catch (final Exception e) {
            final String message = "Transactional error occurred while deleting organization";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

    }


    @Transactional
    public OrganizationModel updateOrganization(final Long orgId, final OrgUpdateDto orgUpdateDto,
                                                final UserDetails userDetails) throws CustomException {
        log.info("Deleting org with id: {} ", orgId);
        final String userUID = userDetails.getUuid();
        Optional<OrganizationModel> organizationModel = getOrgByIdAndUserUID(orgId, userUID);
        if (organizationModel.isEmpty()) {
            final String message = "Org with id: " + orgId + " does not exist";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        try {
            // TODO: to fix update bug related to orgType and capitalowned field.

            OrganizationModel updatedOrg = organizationModel.get();

            Members members = organizationModel.get().getMembers();
            ContactDetails contactDetails = organizationModel.get().getContactDetails();
            AffiliationDetails affiliationDetails = organizationModel.get().getAffiliationDetails();
            SavingsMobilized savingsMobilized = organizationModel.get().getSavingsMobilized();
            OutstandingPortfolio outstandingPortfolio = organizationModel.get().getOutstandingPortfolio();

            CopyUtils.copyProperties(buildMembersFromUpdate(orgUpdateDto),members);
            CopyUtils.copyProperties(buildContactDetailsFromUpdate(orgUpdateDto),contactDetails);
            CopyUtils.copyProperties(buildAffiliationFromUpdate(orgUpdateDto),affiliationDetails);
            CopyUtils.copyProperties(buildSavingsFromUpdate(orgUpdateDto),savingsMobilized);
            CopyUtils.copyProperties(buildOutPortfolioFromUpdate(orgUpdateDto),outstandingPortfolio);

            contactDetailsRepository.save(contactDetails);
            membersRepository.save(members);
            affiliationDetailsRepository.save(affiliationDetails);
            portfolioRepository.save(outstandingPortfolio);
            savingsMobilizedRepository.save(savingsMobilized);

            updatedOrg.setAffiliationDetails(affiliationDetails);
            updatedOrg.setMembers(members);
            updatedOrg.setOutstandingPortfolio(outstandingPortfolio);
            updatedOrg.setContactDetails(contactDetails);
            updatedOrg.setSavingsMobilized(savingsMobilized);
            updatedOrg.setCapitalOwned(orgUpdateDto.getCapitalOwned());
            updatedOrg.setOrganizationType(orgUpdateDto.getOrganizationType());
            updatedOrg.setLastModified(TimeUtil.getZonedDateTimeOfInstant());

            organizationRepository.save(updatedOrg);
            return updatedOrg;
        } catch (final Exception e) {
            final String message = "Transactional error occurred while updating organization";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
    }

    public MemberDashboardContent getDashboardStats(final UserDetails userDetails) {
        final String userUID = userDetails.getUuid();
        return MemberDashboardContent.builder()
                .totalNumberOfOrganizations(getUserTotalOrgs(userUID))
                .numberOfActiveOrgs(getUserTotalOrgsByStatus(userUID, OrganizationStatus.ACTIVE))
                .numberOfInactiveOrgs(getUserTotalOrgsByStatus(userUID, OrganizationStatus.INACTIVE))
                .build();
    }

    private int getUserTotalOrgsByStatus(final String userUID, final OrganizationStatus status) {
        Integer result = organizationRepository.findTotalOrgsStatusForUser(userUID, status.getOrganizationStatus());
        return result == null ? 0 : result;
    }

    private int getUserTotalOrgs(final String userUID) {
        Integer result = organizationRepository.findTotalOrgsForUser(userUID);
        return result == null ? 0 : result;
    }

    private Members buildMembersFromUpdate(final OrgUpdateDto updateDto) {
        return Members.builder()
                .menMembers(updateDto.getMenMembers())
                .womenMembers(updateDto.getWomenMembers())
                .physicallyDisabledMembers(updateDto.getPhysicallyDisabledMembers())
                .youthMembers(updateDto.getYouthMembers())
                .totalMembers(updateDto.getTotalMembers())
                .build();
    }

    private AffiliationDetails buildAffiliationFromUpdate(final OrgUpdateDto updateDto) {
        return AffiliationDetails.builder()
                .affiliationNumber(updateDto.getAffiliationNumber())
                .affiliationCategory(updateDto.getAffiliationCategory())
                .build();
    }

    private ContactDetails buildContactDetailsFromUpdate(final OrgUpdateDto updateDto) {
        return ContactDetails.builder()
                .officeAddress(updateDto.getOfficeAddress())
                .village_ward(updateDto.getVillage_ward())
                .state(updateDto.getState()).lga(updateDto.getLga())
                .city(updateDto.getCity())
                .officialEmail(updateDto.getOfficialEmail())
                .officePhone(updateDto.getOfficePhone())
                .website(updateDto.getWebsite())
                .build();
    }

    private OutstandingPortfolio buildOutPortfolioFromUpdate(final OrgUpdateDto updateDto) {
        return OutstandingPortfolio.builder()
                .menLoan(updateDto.getMenLoan())
                .womenLoan(updateDto.getWomenLoan())
                .youthLoan(updateDto.getYouthLoan())
                .physicallyDisabledLoan(updateDto.getPhysicallyDisabledLoan())
                .totalLoan(updateDto.getTotalLoan())
                .build();
    }

    private SavingsMobilized buildSavingsFromUpdate(final OrgUpdateDto updateDto) {
        return SavingsMobilized.builder()
                .menSavings(updateDto.getMenSavings())
                .womenSavings(updateDto.getWomenSavings())
                .physicallyDisabledSavings(updateDto.getPhysicallyDisabledSavings())
                .youthSavings(updateDto.getYouthSavings())
                .totalSavings(updateDto.getTotalSavings())
                .build();
    }


    private Optional<OrganizationModel> getOrgByIdAndUserUID(final Long orgId, final String userUID) {
        return  organizationRepository.findByIdAndAuthorizedRepresentativeId(orgId, userUID);
    }

}
