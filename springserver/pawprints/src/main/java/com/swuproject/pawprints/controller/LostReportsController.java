package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-reports")
public class LostReportsController {

    @Autowired
    private LostReportsService lostReportsService;

    @GetMapping
    public List<LostReports> getRandomLostReports() {
        return lostReportsService.getRandomLostReports();
    }
}