package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LostReportsService {
    @Autowired
    private LostReportsRepository lostReportsRepository;

    public List<LostReports> getAllLostReports() {
        return lostReportsRepository.findAll();
    }
}
