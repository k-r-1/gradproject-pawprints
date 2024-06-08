package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lost_reports")
public class LostReportsController {

    private final LostReportsService lostReportsService;

    public LostReportsController(LostReportsService lostReportsService) {
        this.lostReportsService = lostReportsService;
    }

    @GetMapping("/{petId}")
    public LostReports getLostReportByPetId(@PathVariable int petId) {
        return lostReportsService.getLostReportByPetId(petId);
    }
}
