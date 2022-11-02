package com.nimfid.modelservice.util;

import com.nimfid.modelservice.repo.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgUIDUtil {

    @Autowired
    private final OrganizationRepository organizationRepository;


    private boolean isUuidPresent(final String orgUid) {
        return organizationRepository.existsByOrgUID(orgUid);
    }

    public String assignUuid() {
        String uuid;
        do {
            uuid = generateRandomId();
        } while (isUuidPresent(uuid));
        return uuid;
    }

    private String generateRandomId() {
        final String preUuid = "ORG";
        final int SHORT_ID_LENGTH = 8;
        final String shortId = RandomStringUtils.randomNumeric(SHORT_ID_LENGTH);
        return preUuid.concat(shortId);
    }

}
