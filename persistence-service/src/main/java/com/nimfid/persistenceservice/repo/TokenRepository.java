package com.nimfid.persistenceservice.repo;


import com.nimfid.persistenceservice.data.TokenStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenStore, Long> {

    @Modifying
    @Query(value = "DELETE from Token_Store t WHERE t.uuid = ?1 AND t.token = ?2",nativeQuery = true)
    void deleteTokenFromStore(String uuid, String token);

    boolean existsByUuidAndToken(String uuid, String token);
}