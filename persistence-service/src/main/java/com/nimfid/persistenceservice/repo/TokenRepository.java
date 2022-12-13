package com.nimfid.persistenceservice.repo;


import com.nimfid.persistenceservice.data.TokenStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenStore, Long> {

    /*@Modifying
    @Query(value = "DELETE from Token_Store t WHERE t.uuid = ?1 AND t.token = ?2",nativeQuery = true)
    void deleteTokenFromStore(String uuid, String token);*/
    void deleteAllByUuidAndToken(String uuid, String token);
    /*@Modifying
    @Query(value = "DELETE from Token_Store t WHERE t.token = ?1",nativeQuery = true)
    void deleteToken(String token);*/
    void deleteAllByToken(String token);

    boolean existsByUuidAndToken(String uuid, String token);
    boolean existsByToken(String token);


}