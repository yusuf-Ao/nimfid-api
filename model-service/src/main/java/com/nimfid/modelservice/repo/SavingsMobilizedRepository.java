package com.nimfid.modelservice.repo;


import com.nimfid.modelservice.data.SavingsMobilized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsMobilizedRepository extends JpaRepository<SavingsMobilized, Long> {
}
