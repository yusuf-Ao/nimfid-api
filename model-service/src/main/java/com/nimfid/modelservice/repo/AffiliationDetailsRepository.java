package com.nimfid.modelservice.repo;


import com.nimfid.modelservice.data.AffiliationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffiliationDetailsRepository extends JpaRepository<AffiliationDetails, Long> {
}
