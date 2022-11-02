package com.nimfid.persistenceservice.repo;


import com.nimfid.persistenceservice.data.ForgotPasswordOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordOTP, Long> {

    Optional<ForgotPasswordOTP> findByOtpAndUserId(String otp, Long userId);
    Optional<ForgotPasswordOTP> findByUserId(Long userId);

    void deleteByOtpAndUserId(String otp, Long userId);
    void deleteByUserId(Long userId);
}
