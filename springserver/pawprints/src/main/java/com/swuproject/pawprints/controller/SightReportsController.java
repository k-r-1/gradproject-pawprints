package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.SightReports;
import com.swuproject.pawprints.domain.SightReportsImage;
import com.swuproject.pawprints.dto.SightReportsEditResponse;
import com.swuproject.pawprints.dto.SightReportsImageResponse;
import com.swuproject.pawprints.dto.SightReportsResponse;
import com.swuproject.pawprints.repository.SightReportsRepository;
import com.swuproject.pawprints.repository.SightReportsImageRepository;
import com.swuproject.pawprints.service.GCSUploaderService;
import com.swuproject.pawprints.service.SightReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
public class SightReportsController {

    @Autowired
    private SightReportsService sightReportsService;

    @Autowired
    private GCSUploaderService gcsUploaderService;

    @Autowired
    private SightReportsRepository sightReportsRepository;

    @Autowired
    private SightReportsImageRepository sightReportsImageRepository;

    @GetMapping("/sightReports")
    public List<SightReportsResponse> getSightReports() {
        return sightReportsService.getAllSightReports();
    }

    @GetMapping("/sightReports/{sightId}")
    public List<SightReportsResponse> getSightReports(@PathVariable int sightId) {
        return sightReportsService.getSightReportBySightId(sightId);
    }

    @DeleteMapping("/sightReports/{sightId}")
    public ResponseEntity<Void> deleteSightReport(@PathVariable int sightId) {
        try {
            sightReportsService.deleteSightById(sightId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/sightReports/edit/{sightId}")
    public ResponseEntity<Void> updateSightReport(@PathVariable int sightId, @RequestBody SightReportsEditResponse sightReportsEditResponse) {
        boolean updated = sightReportsService.updateSightReport(sightId, sightReportsEditResponse);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/sightReportsPost")
    @Transactional  // 트랜잭션 관리 추가
    public ResponseEntity<SightReportsResponse> createSightReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("sightTitle") String sightTitle,
            @RequestParam("sightType") String sightType,
            @RequestParam("sightBreed") String sightBreed,
            @RequestParam("sightAreaLat") Double sightAreaLat,
            @RequestParam("sightAreaLng") Double sightAreaLng,
            @RequestParam("sightDate") String sightDate,
            @RequestParam("sightLocation") String sightLocation,
            @RequestParam("sightDescription") String sightDescription,
            @RequestParam("sightContact") String sightContact
    ) throws IOException, ParseException {

        // sightType을 "dog" 또는 "cat"으로 변환
        String formattedSightType = sightType.equals("개") ? "dog" : (sightType.equals("고양이") ? "cat" : sightType);

        // 날짜 문자열을 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(sightDate);

        // 단일 이미지 파일을 GCS에 업로드
        String folderName = "sight_reports_image/" + formattedSightType;
        String imageUrl;
        try {
            imageUrl = gcsUploaderService.uploadFile(file, folderName);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }

        // SightReports 객체 생성 및 저장
        SightReports sightReport = new SightReports();
        sightReport.setUserId(userId);
        sightReport.setSightTitle(sightTitle);
        sightReport.setSightType(formattedSightType);
        sightReport.setSightBreed(sightBreed);
        sightReport.setSightAreaLat(sightAreaLat);
        sightReport.setSightAreaLng(sightAreaLng);
        sightReport.setSightDate(parsedDate);
        sightReport.setSightLocation(sightLocation);
        sightReport.setSightDescription(sightDescription);
        sightReport.setSightContact(sightContact);
        sightReportsRepository.save(sightReport);

        // 이미지 정보를 DB에 저장
        SightReportsImage sightReportsImage = new SightReportsImage();
        sightReportsImage.setSightReports(sightReport);
        sightReportsImage.setSightImagePath(imageUrl);
        sightReportsImageRepository.save(sightReportsImage);

        // SightReportsResponse 객체 생성
        SightReportsResponse response = new SightReportsResponse(
                sightReport.getSightId(),
                sightReport.getUserId(),
                sightReport.getSightTitle(),
                sightReport.getSightBreed(),
                sightReport.getSightAreaLat(),
                sightReport.getSightAreaLng(),
                sightReport.getSightDate(),
                sightReport.getSightLocation(),
                sightReport.getSightDescription(),
                sightReport.getSightContact(),
                List.of(new SightReportsImageResponse(sightReportsImage.getSightImageId(), imageUrl))
        );

        return ResponseEntity.ok(response);
    }
}