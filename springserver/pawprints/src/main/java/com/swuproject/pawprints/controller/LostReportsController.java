package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LostReportsController {
    @Autowired
    private LostReportsService lostReportsService;

    @GetMapping("/lostReports")
    public List<LostReports> getLostReports() {
        return lostReportsService.getAllLostReports();
    }
}
