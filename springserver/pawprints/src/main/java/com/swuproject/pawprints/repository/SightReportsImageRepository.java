package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.SightReportsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SightReportsImageRepository extends JpaRepository<SightReportsImage, Integer> {
    @Query("SELECT s FROM SightReportsImage s WHERE s.sightReports.sightId = :sightId")
    List<SightReportsImage> findBySightId(@Param("sightId") int sightId);

}
