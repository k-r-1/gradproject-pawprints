package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.dto.SightReportsResponse;
import com.swuproject.pawprints.service.SightReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SightReportsController {

    @Autowired
    private SightReportsService sightReportsService;

    @GetMapping("/api/sightReports")
    public List<SightReportsResponse> getSightReports() {
        return sightReportsService.getAllSightReports();
    }
}
