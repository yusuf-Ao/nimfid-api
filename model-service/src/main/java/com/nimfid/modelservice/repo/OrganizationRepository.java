package com.nimfid.modelservice.repo;

import com.nimfid.modelservice.data.OrganizationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    @Query(value = "SELECT o.org_name, o.organization_type, o.organization_status, o.date_registered, " +
            "c.state, c.lga, c.city, c.office_address, c.website, a.affiliation_category " +
            "FROM organization_model o " +
            "LEFT JOIN contact_details c " +
            "ON o.contact_details_id = c.id " +
            "LEFT JOIN affiliation_details a " +
            "ON o.affiliation_details_id = a.id " +
            "WHERE ((:name) IS NULL OR o.org_name LIKE %:name%) " +
            "AND (c.state IN (:state) OR COALESCE(:state, null) IS NULL) " +
            "AND (o.organization_type IN (:org) OR COALESCE(:org, null) IS NULL) " +
            "AND (a.affiliation_category IN (:cat) OR COALESCE(:cat, null) IS NULL) " +
            "ORDER BY o.date_registered DESC",
            countQuery = "SELECT count(*) FROM organization_model o JOIN contact_details c " +
                    "ON o.contact_details_id = c.id JOIN affiliation_details a " +
                    "ON o.affiliation_details_id = a.id ORDER BY o.date_registered DESC",
            nativeQuery = true)
    Page<Map<String, Object>> findAllForPublic(@Param("name") final String query, @Param("state") final Collection<String> location,
                                               @Param("org") final Collection<String> orgTypes,
                                               @Param("cat") final Collection<String> categories, Pageable pageable);
    @Modifying
    @Query(value = "SELECT o.org_name, o.organization_type, o.organization_status, o.date_registered, " +
            "c.state, c.lga, c.city, c.office_address, c.website, c.latitude, c.longitude, a.affiliation_category " +
            "FROM organization_model o " +
            "LEFT JOIN contact_details c " +
            "ON o.contact_details_id = c.id " +
            "LEFT JOIN affiliation_details a " +
            "ON o.affiliation_details_id = a.id " +
            "WHERE ((:name) IS NULL OR o.org_name LIKE %:name%) " +
            "AND (c.state IN (:state) OR COALESCE(:state, null) IS NULL) " +
            "AND (o.organization_type IN (:org) OR COALESCE(:org, null) IS NULL) " +
            "AND (a.affiliation_category IN (:cat) OR COALESCE(:cat, null) IS NULL) " +
            "ORDER BY o.date_registered DESC",
            nativeQuery = true)
    List<Map<String, Object>> findAllForPublicMap(@Param("name") final String query, @Param("state") final Collection<String> location,
                                                  @Param("org") final Collection<String> orgTypes,
                                                  @Param("cat") final Collection<String> categories);



    //todo sanitize all queries against sql injection
}
