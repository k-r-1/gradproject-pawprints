package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.LostReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LostReportsRepository extends JpaRepository<LostReports, Long> {
}