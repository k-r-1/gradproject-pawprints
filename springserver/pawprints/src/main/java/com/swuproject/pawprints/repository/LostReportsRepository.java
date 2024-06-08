package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.LostReports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostReportsRepository extends JpaRepository<LostReports, Long> {
}