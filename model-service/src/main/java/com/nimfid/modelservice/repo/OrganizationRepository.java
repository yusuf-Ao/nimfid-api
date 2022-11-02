package com.nimfid.modelservice.repo;

import com.nimfid.modelservice.data.OrganizationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationModel, Long> {
    boolean existsByOrgUID(String orgUID);

    boolean existsByOrgName(String orgName);

    @Query(value = "SELECT COUNT(*) from organization_model o WHERE o.authorized_representative_id = ?1 " +
            "AND o.organization_status = ?2", nativeQuery = true)
    Integer findTotalOrgsStatusForUser(String userUID, String status);

    @Query(value = "SELECT COUNT(*) from organization_model o WHERE o.authorized_representative_id = ?1 ", nativeQuery = true)
    Integer findTotalOrgsForUser(String userUID);


    @Query(value = "SELECT COUNT(*) from organization_model o WHERE o.organization_status = ?1", nativeQuery = true)
    Integer findTotalOrgsStatus(String status);

    @Query(value = "SELECT COUNT(*) from organization_model", nativeQuery = true)
    Integer findTotalOrgs();

    Page<OrganizationModel> findAllByAuthorizedRepresentativeId(String userUID, Pageable pageable);

    Optional<OrganizationModel> findByIdAndAuthorizedRepresentativeId(Long orgId, String userUID);
}
