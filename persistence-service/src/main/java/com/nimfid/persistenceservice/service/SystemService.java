package com.nimfid.persistenceservice.service;


import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.exception.CustomException;
import com.nimfid.commons.request.UserDetails;
import com.nimfid.commons.response.PageResponse;
import com.nimfid.persistenceservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
