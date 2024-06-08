package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.dto.LostReportsDTO;
import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.service.LostReportsService;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lostReports")
public class LostReportsController {

    @Autowired
    private LostReportsRepository lostReportsRepository;

    @Autowired
    private LostReportsService lostReportsService;

    @GetMapping
    public List<LostReportsDTO> getAllLostReports() {
        List<LostReports> lostReports = lostReportsRepository.findAll();
        return lostReports.stream()
                .map(lostReportsService::convertToDTO)
                .collect(Collectors.toList());
    }
}
