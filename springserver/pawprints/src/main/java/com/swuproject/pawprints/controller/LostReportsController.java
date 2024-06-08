package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.dto.LostReportsResponse;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LostReportsController {

    @Autowired
    private LostReportsService lostReportsService;

    @GetMapping("/api/lostReports")
    public List<LostReportsResponse> getSightReports() {
        return lostReportsService.getAllLostReports();
    }
}
