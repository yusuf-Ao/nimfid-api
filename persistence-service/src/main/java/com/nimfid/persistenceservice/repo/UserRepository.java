package com.nimfid.persistenceservice.repo;


import com.nimfid.commons.data.UserStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserStore, Long> {

    Optional<UserStore> findByEmail(String email);
    Optional<UserStore> findByUuid(String uuid);
    Optional<UserStore> findByIdAndUuid(Long id, String uuid);

    Page<UserStore> findAll(Pageable pageable);

    boolean existsByUuid(String uuid);

    @Modifying
    @Query(value = "UPDATE user_store u SET u.account_status=?1, u.user_status=?2 " +
            "WHERE u.email=?3", nativeQuery = true)
    void updateVerificationDetails(String accountStatus, String userStatus, String email);

    @Modifying
    @Query(value = "UPDATE user_store u SET u.password=?2, u.password_update_date=?3 " +
            "WHERE u.email=?1", nativeQuery = true)
    void updatePassword(String email, String password, ZonedDateTime dateTime);

}
