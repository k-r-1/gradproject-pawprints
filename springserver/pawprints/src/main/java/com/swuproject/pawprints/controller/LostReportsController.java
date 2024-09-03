package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.dto.LostReportsResponse;
import com.swuproject.pawprints.dto.LostReportsImageResponse;
import com.swuproject.pawprints.repository.LostReportsImageRepository;
import com.swuproject.pawprints.repository.LostReportsRepository;
import com.swuproject.pawprints.service.GCSUploaderService;
import com.swuproject.pawprints.service.LostReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LostReportsController {

    @Autowired
    private LostReportsService lostReportsService;

    @Autowired
    private GCSUploaderService gcsUploaderService;

    @Autowired
    private LostReportsRepository lostReportsRepository;

    @Autowired
    private LostReportsImageRepository lostReportsImageRepository;

    @GetMapping("/lostReports")
    public List<LostReportsResponse> getLostReports() {
        return lostReportsService.getAllLostReports();
    }

    @GetMapping("/lost_reports/{petId}")
    public ResponseEntity<LostReportsResponse> getLostReportByPetId(@PathVariable int petId) {
        LostReportsResponse lostReport = lostReportsService.getLostReportByPetId(petId);
        return ResponseEntity.ok(lostReport);
    }

    @PostMapping("/lostReportsPost")
    @Transactional  // 트랜잭션 관리 추가
    public ResponseEntity<LostReportsResponse> createLostReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("petId") int petId,
            @RequestParam("lostTitle") String lostTitle,
            @RequestParam("petType") String petType,
            @RequestParam("lostAreaLat") Double lostAreaLat,
            @RequestParam("lostAreaLng") Double lostAreaLng,
            @RequestParam("lostDate") String lostDate,
            @RequestParam("lostLocation") String lostLocation,
            @RequestParam("lostDescription") String lostDescription,
            @RequestParam("lostContact") String lostContact
    ) throws IOException, ParseException {

        // 날짜 문자열을 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(lostDate);

        // 단일 이미지 파일을 GCS에 업로드
        String folderName = "lost_reports_image/" + petType;
        String imageUrl;
        try {
            imageUrl = gcsUploaderService.uploadFile(file, folderName);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }

        // LostReports 객체 생성 및 저장
        LostReports lostReport = new LostReports();
        lostReport.setPetId(petId);
        lostReport.setLostTitle(lostTitle);
        lostReport.setLostAreaLat(lostAreaLat);
        lostReport.setLostAreaLng(lostAreaLng);
        lostReport.setLostDate(parsedDate);
        lostReport.setLostLocation(lostLocation);
        lostReport.setLostDescription(lostDescription);
        lostReport.setLostContact(lostContact);
        lostReportsRepository.save(lostReport);

        // 이미지 정보를 DB에 저장
        LostReportsImage lostReportsImage = new LostReportsImage();
        lostReportsImage.setLostReports(lostReport);
        lostReportsImage.setLostImagePath(imageUrl);
        lostReportsImageRepository.save(lostReportsImage);

        // LostReportsResponse 객체 생성
        LostReportsResponse response = new LostReportsResponse(
                lostReport.getLostId(),
                lostReport.getPetId(),
                lostReport.getLostTitle(),
                lostReport.getPetBreed(),
                lostReport.getLostAreaLat(),
                lostReport.getLostAreaLng(),
                lostReport.getLostDate(),
                lostReport.getLostLocation(),
                lostReport.getLostDescription(),
                lostReport.getLostContact(),
                List.of(new LostReportsImageResponse(lostReportsImage.getLostImageId(), imageUrl))
        );

        return ResponseEntity.ok(response);
    }

}