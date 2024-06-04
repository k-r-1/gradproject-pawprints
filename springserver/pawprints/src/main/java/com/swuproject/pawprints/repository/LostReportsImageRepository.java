package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.LostReportsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostReportsImageRepository extends JpaRepository<LostReportsImage, Long> {
    List<LostReportsImage> findByLostId(Long lostId);
}
