package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class LostReportsService {
    @Autowired
    private LostReportsRepository lostReportsRepository;

    public List<LostReports> getAllLostReports() {
        return lostReportsRepository.findAll();
    }

    private static final Logger logger = LoggerFactory.getLogger(LostReportsService.class);

    public LostReports getLostReportById(Long id) {
        LostReports lostReport = lostReportsRepository.findById(id).orElse(null);
        logger.info("Fetched LostReport: {}", lostReport);
        return lostReport;
    }
}
