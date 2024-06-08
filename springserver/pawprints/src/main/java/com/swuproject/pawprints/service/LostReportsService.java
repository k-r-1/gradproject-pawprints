package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.stereotype.Service;

@Service
public class LostReportsService {

    private final LostReportsRepository lostReportsRepository;

    public LostReportsService(LostReportsRepository lostReportsRepository) {
        this.lostReportsRepository = lostReportsRepository;
    }

    public LostReports getLostReportByPetId(int petId) {
        return lostReportsRepository.findByPetId(petId);
    }
}
