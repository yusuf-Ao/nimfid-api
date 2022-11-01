package com.nimfid.persistenceservice.repo;


import com.nimfid.persistenceservice.data.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByCodeAndUserId(String code, Long userId);
    Optional<VerificationCode> findByUserId(Long userId);

    void deleteByCodeAndUserId(String code, Long userId);
    void deleteByUserId(Long userId);
}
