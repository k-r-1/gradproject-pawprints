package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.SightReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LostReportsRepository extends JpaRepository<LostReports, Integer> {
    LostReports findByPetId(int petId);
    @Query("SELECT l FROM LostReports l WHERE l.lostId = :id")
    List<LostReports> findLostReportsByLostId(int id);
}
