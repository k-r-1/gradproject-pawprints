package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.SightReports;
import com.swuproject.pawprints.domain.SightReportsImage;
import com.swuproject.pawprints.dto.SightReportsEditResponse;
import com.swuproject.pawprints.dto.SightReportsImageResponse;
import com.swuproject.pawprints.dto.SightReportsResponse;
import com.swuproject.pawprints.repository.SightReportsImageRepository;
import com.swuproject.pawprints.repository.SightReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SightReportsService {

    @Autowired
    private SightReportsRepository sightReportsRepository;

    @Autowired
    private SightReportsImageRepository sightReportsImageRepository;

    @Autowired
    private GCSUploaderService gcsUploaderService;  // GCSUploaderService 주입

    public List<SightReportsResponse> getAllSightReports() {
        List<SightReports> sightReportsList = sightReportsRepository.findAll();

        return sightReportsList.stream().map(sightReport -> {
            SightReportsResponse response = new SightReportsResponse();
            response.setUserId(sightReport.getUserId());
            response.setSightId(sightReport.getSightId());
            response.setSightTitle(sightReport.getSightTitle());
            response.setSightBreed(sightReport.getSightBreed());
            response.setSightAreaLat(sightReport.getSightAreaLat());
            response.setSightAreaLng(sightReport.getSightAreaLng());
            response.setSightDate(sightReport.getSightDate());
            response.setSightLocation(sightReport.getSightLocation());
            response.setSightDescription(sightReport.getSightDescription());
            response.setSightContact(sightReport.getSightContact());
            response.setSightImages(
                    sightReport.getSightImages().stream()
                            .map(image -> {
                                SightReportsImageResponse imageResponse = new SightReportsImageResponse();
                                imageResponse.setSightImageId(image.getSightImageId());
                                imageResponse.setSightImagePath(image.getSightImagePath());
                                return imageResponse;
                            }).collect(Collectors.toList())
            );
            return response;
        }).collect(Collectors.toList());
    }

    public List<SightReportsResponse> getSightReportBySightId(int sightId) {
        List<SightReports> sightReportsList = sightReportsRepository.findAll();

        return sightReportsList.stream()
                .filter(sightReport -> sightReport.getSightId() == sightId)
                .map(sightReport -> {
                    SightReportsResponse response = new SightReportsResponse();
                    response.setUserId(sightReport.getUserId());
                    response.setSightId(sightReport.getSightId());
                    response.setSightTitle(sightReport.getSightTitle());
                    response.setSightBreed(sightReport.getSightBreed());
                    response.setSightAreaLat(sightReport.getSightAreaLat());
                    response.setSightAreaLng(sightReport.getSightAreaLng());
                    response.setSightDate(sightReport.getSightDate());
                    response.setSightLocation(sightReport.getSightLocation());
                    response.setSightDescription(sightReport.getSightDescription());
                    response.setSightContact(sightReport.getSightContact());
                    response.setSightImages(
                            sightReport.getSightImages().stream()
                                    .map(image -> {
                                        SightReportsImageResponse imageResponse = new SightReportsImageResponse();
                                        imageResponse.setSightImageId(image.getSightImageId());
                                        imageResponse.setSightImagePath(image.getSightImagePath());
                                        return imageResponse;
                                    }).collect(Collectors.toList())
                    );
                    return response;
                }).collect(Collectors.toList());
    }

    public boolean updateSightReport(int sightId, SightReportsEditResponse sightReportsEditResponse) {
        Optional<SightReports> existingReport = sightReportsRepository.findById(sightId);
        if (existingReport.isPresent()) {
            SightReports report = existingReport.get();

            // DTO에서 데이터를 엔티티로 변환하여 업데이트
            report.setSightTitle(sightReportsEditResponse.getTitle());
            report.setSightBreed(sightReportsEditResponse.getBreed());
            report.setSightDate(sightReportsEditResponse.getDate());
            report.setSightLocation(sightReportsEditResponse.getLocation());
            report.setSightDescription(sightReportsEditResponse.getFeature());
            report.setSightContact(sightReportsEditResponse.getContact());
            report.setSightAreaLat(sightReportsEditResponse.getLatitude());
            report.setSightAreaLng(sightReportsEditResponse.getLongitude());

            // 데이터베이스에 저장
            sightReportsRepository.save(report);
            return true;
        }
        return false;
    }

    public void deleteSightById(int sightId) {
        // sightReportsImageRepository에서 sightId에 해당하는 이미지들을 찾음
        //List<SightReportsImage> images = sightReportsImageRepository.findBySightId(sightId);

        // GCS에서 파일 삭제
        //for (SightReportsImage image : images) {
            //String imagePath = image.getSightImagePath();
            //try {
            //    gcsUploaderService.deleteFile(imagePath);  // GCS에서 이미지 삭제
              //  System.out.println("이미지 삭제 완료");
            //} catch (IOException e) {
                //throw new RuntimeException("GCS에서 이미지 삭제 실패: " + imagePath, e);
           // }
       // }
        try {
            // sightId에 해당하는 SightReportsImage 데이터를 삭제
            List<SightReportsImage> sightReportsImages = sightReportsImageRepository.findBySightId(sightId);
            for (SightReportsImage image : sightReportsImages) {
                sightReportsImageRepository.delete(image);
            }

            // sightId에 해당하는 SightReports 데이터를 삭제
            sightReportsRepository.deleteById(sightId);

            System.out.println("데이터베이스에서 sightId " + sightId + "에 해당하는 보고서 및 이미지 삭제 완료");
        } catch (Exception e) {
            // 오류 처리
            e.printStackTrace();
            System.out.println("삭제 중 오류 발생: " + e.getMessage());
        }
    }
}
