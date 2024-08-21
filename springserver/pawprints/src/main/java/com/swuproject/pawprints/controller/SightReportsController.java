package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.SightReports;
import com.swuproject.pawprints.domain.SightReportsImage;
import com.swuproject.pawprints.dto.SightReportsImageResponse;
import com.swuproject.pawprints.dto.SightReportsResponse;
import com.swuproject.pawprints.repository.SightReportsRepository;
import com.swuproject.pawprints.repository.SightReportsImageRepository;
import com.swuproject.pawprints.service.GCSUploaderService;
import com.swuproject.pawprints.service.SightReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class SightReportsController {

    @Autowired
    private SightReportsService sightReportsService;

    @Autowired
    private GCSUploaderService gcsUploaderService;

    @Autowired
    private SightReportsRepository sightReportsRepository;

    @Autowired
    private SightReportsImageRepository sightReportsImageRepository;

    @GetMapping("/api/sightReports")
    public List<SightReportsResponse> getSightReports() {
        return sightReportsService.getAllSightReports();
    }

    @PostMapping("/api/sightReports")
    public ResponseEntity<SightReportsResponse> createSightReport(
            @RequestParam("file") List<MultipartFile> files,  // 여러 파일을 받을 수 있도록 List로 수정
            @RequestParam("sightType") String sightType,
            @RequestParam("sightBreed") String sightBreed,
            @RequestParam("sightAreaLat") Double sightAreaLat,
            @RequestParam("sightAreaLng") Double sightAreaLng,
            @RequestParam("sightDate") Date sightDate,
            @RequestParam("sightLocation") String sightLocation,
            @RequestParam("sightDescription") String sightDescription
    ) throws IOException {

        // DB에 SightReports 객체를 저장
        SightReports sightReport = new SightReports();
        sightReport.setSightType(sightType);
        sightReport.setSightBreed(sightBreed);
        sightReport.setSightAreaLat(sightAreaLat);
        sightReport.setSightAreaLng(sightAreaLng);
        sightReport.setSightDate(sightDate);
        sightReport.setSightLocation(sightLocation);
        sightReport.setSightDescription(sightDescription);
        sightReportsRepository.save(sightReport);

        // 이미지 URL 리스트 생성
        List<SightReportsImageResponse> imageResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            // 이미지 파일을 GCS에 업로드
            String folderName = "sight_reports_image/" + (sightType.equals("개") ? "dog" : "cat");
            String imageUrl = gcsUploaderService.uploadFile(file, folderName);

            // 이미지 URL을 DB에 저장
            SightReportsImage sightReportsImage = new SightReportsImage();
            sightReportsImage.setSightReports(sightReport);
            sightReportsImage.setSightImagePath(imageUrl);
            sightReportsImageRepository.save(sightReportsImage);

            // SightReportsImageResponse 객체 생성
            imageResponses.add(new SightReportsImageResponse(sightReportsImage.getSightImageId(), imageUrl));
        }

        // SightReportsResponse 객체 생성
        SightReportsResponse response = new SightReportsResponse(
                sightReport.getSightId(),
                sightReport.getSightTitle(),  // sightTitle 필드는 추가할 수 있습니다.
                sightReport.getSightBreed(),
                sightReport.getSightAreaLat(),
                sightReport.getSightAreaLng(),
                sightReport.getSightDate(),
                sightReport.getSightLocation(),
                sightReport.getSightDescription(),
                imageResponses // 이미지 리스트를 포함
        );

        return ResponseEntity.ok(response);
    }
}
