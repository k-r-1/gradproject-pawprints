package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.repository.LostReportsRepository;
import com.swuproject.pawprints.repository.LostReportsImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LostReportsService {
    private final LostReportsRepository lostReportsRepository;
    private final LostReportsImageRepository lostReportsImageRepository;

    public LostReportsService(LostReportsRepository lostReportsRepository, LostReportsImageRepository lostReportsImageRepository) {
        this.lostReportsRepository = lostReportsRepository;
        this.lostReportsImageRepository = lostReportsImageRepository;
    }

    public List<LostReports> getAllLostReports() {
        return lostReportsRepository.findAll();
    }

    public List<LostReportsImage> getImagesByLostId(Long lostId) {
        return lostReportsImageRepository.findByLostId(lostId);
    }
}
