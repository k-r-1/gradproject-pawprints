package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.SightReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SightReportsRepository extends JpaRepository<SightReports, Integer> {

    @Query("SELECT s FROM SightReports s WHERE s.sightId = :id")
    List<SightReports> findSightReportsBySightId(int id);
}