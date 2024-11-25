package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.domain.SightReportsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LostReportsImageRepository extends JpaRepository<LostReportsImage, Integer> {

    @Query("SELECT l FROM LostReportsImage l WHERE l.lostReports.lostId = :lostId")
    List<LostReportsImage> findByLostId(@Param("lostId") int lostId);
}
