package com.nimfid.apigateway.service;

import com.nimfid.commons.data.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudCheckRepo extends JpaRepository<UserStore, Long> {

    Optional<UserStore> findByIdAndUuid(Long id, String uuid);
}
