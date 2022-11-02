package com.nimfid.persistenceservice.service;


import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.enums.UserStatus;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.commons.response.SystemDashboardContent;
import com.nimfid.persistenceservice.repo.UserRepository;
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
public class SystemService {

    @Autowired
    private final UserRepository userRepository;

    public PageResponse getAllUsers(final int page, final int size) {
        log.info("Fetching all users");
        Pageable pageable = PageRequest.of(page, size);
        Page<UserStore> userPage = userRepository.findAll(pageable);
        List<UserStore> users = userPage.getContent();
        return PageResponse.builder()
                .pageContent(users)
                .currentPage(userPage.getNumber())
                .totalItems(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }

    public SystemDashboardContent getPartialContent() {
        return SystemDashboardContent.builder()
                .totalNumberOfUsers(getTotalUsers())
                .numberOfActiveUsers(getTotalUserByStatus(UserStatus.ACTIVE))
                .numberOfInactiveUsers(getTotalUserByStatus(UserStatus.INACTIVE))
                .build();
    }

    private int getTotalUserByStatus(final UserStatus status) {
        Integer result = userRepository.findTotalUsersStatus(status.getUserStatus());
        return result == null ? 0 : result;
    }

    private int getTotalUsers() {
        Integer result = userRepository.findTotalUsers();
        return result == null ? 0 : result;
    }

    /*public UserStore getUser(final Long userId) throws CustomException {
        log.info("Getting user profile method");
        Optional<UserStore> userModel = userRepository.findById(userId);
        if (userModel.isEmpty()) {
            final String message = "User does not exists in the database.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_FOUND, message);
        }
        log.info("User Profile fetched successfully");
        return userModel.get();
    }*/
}
