package com.nimfid.persistenceservice.util;

import com.nimfid.persistenceservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UUIDUtil {

    @Autowired
    private final UserRepository userRepository;

    private boolean isUuidPresent(final String uuid) {
        return userRepository.existsByUuid(uuid);
    }
    public String assignUuid() {
        String uuid;
        do {
            uuid = generateRandomId();
        } while (isUuidPresent(uuid));
        return uuid;
    }

    private String generateRandomId() {
        final String preUuid = "MID";
        final int SHORT_ID_LENGTH = 8;
        final String shortId = RandomStringUtils.randomNumeric(SHORT_ID_LENGTH);
        return preUuid.concat(shortId);
    }

}
