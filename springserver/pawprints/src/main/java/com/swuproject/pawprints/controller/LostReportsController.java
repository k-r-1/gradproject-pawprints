package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lost-reports")
public class LostReportsController {

    @Autowired
    private LostReportsService lostReportsService;

    @GetMapping
    public ResponseEntity<List<LostReports>> getAllLostReports() {
        List<LostReports> lostReportsList = lostReportsService.getAllLostReports();
        return ResponseEntity.ok(lostReportsList);
    }

    @GetMapping("/images")
    public ResponseEntity<List<LostReportsImage>> getLostReportImages(Long lostId) {
        List<LostReportsImage> images = lostReportsService.getImagesByLostId(lostId);
        return ResponseEntity.ok(images);
    }
}
