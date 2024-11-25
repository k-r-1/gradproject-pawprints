package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.Pet;
import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.dto.*;
import com.swuproject.pawprints.repository.LostReportsRepository;
import com.swuproject.pawprints.repository.LostReportsImageRepository;
import com.swuproject.pawprints.repository.PetRepository;
import com.swuproject.pawprints.repository.SightReportsImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LostReportsService {

    @Autowired
    private LostReportsRepository lostReportsRepository;

    @Autowired
    private LostReportsImageRepository lostReportsImageRepository;

    @Autowired
    private PetRepository petRepository;

    public List<LostReportsResponse> getAllLostReports() {
        List<LostReports> lostReportsList = lostReportsRepository.findAll();

        return lostReportsList.stream().map(lostReports -> {
            LostReportsResponse response = new LostReportsResponse();
            PetResponse petResponse = new PetResponse();
            response.setLostId(lostReports.getLostId());
            response.setPetId(lostReports.getPetId());
            List<Pet> petList = petRepository.findLostPetsByPetId(lostReports.getPetId());
            response.setUserId(petList.get(0).getUserId());
            response.setPetBreed(petList.get(0).getBreed());
            response.setPetGender(petList.get(0).getGender());
            response.setPetAge(petList.get(0).getAge());
            response.setPetFeature(petList.get(0).getFeature());
            response.setLostTitle(lostReports.getLostTitle());
            response.setLostAreaLat(lostReports.getLostAreaLat());
            response.setLostAreaLng(lostReports.getLostAreaLng());
            response.setLostDate(lostReports.getLostDate());
            response.setLostLocation(lostReports.getLostLocation());
            response.setLostDescription(lostReports.getLostDescription());
            response.setLostContact(lostReports.getLostContact());
            response.setLostImages(
                    lostReports.getLostImages().stream()
                            .map(image -> {
                                LostReportsImageResponse imageResponse = new LostReportsImageResponse();
                                imageResponse.setLostImageId(image.getLostImageId());
                                imageResponse.setLostImagePath(image.getLostImagePath());
                                return imageResponse;
                            }).collect(Collectors.toList())
            );

            return response;
        }).collect(Collectors.toList());
    }

    public LostReportsResponse getLostReportByPetId(int petId) {
        LostReports lostReport = lostReportsRepository.findByPetId(petId);

        LostReportsResponse response = new LostReportsResponse();
        response.setLostId(lostReport.getLostId());
        response.setPetId(lostReport.getPetId());
        response.setPetBreed(lostReport.getPet().getBreed());
        response.setPetGender(lostReport.getPet().getGender());
        response.setPetAge(lostReport.getPet().getAge());
        response.setPetFeature(lostReport.getPet().getFeature());
        response.setLostTitle(lostReport.getLostTitle());
        response.setLostAreaLat(lostReport.getLostAreaLat());
        response.setLostAreaLng(lostReport.getLostAreaLng());
        response.setLostDate(lostReport.getLostDate());
        response.setLostLocation(lostReport.getLostLocation());
        response.setLostDescription(lostReport.getLostDescription());
        response.setLostContact(lostReport.getLostContact());
        response.setLostImages(
                lostReport.getLostImages().stream()
                        .map(image -> {
                            LostReportsImageResponse imageResponse = new LostReportsImageResponse();
                            imageResponse.setLostImageId(image.getLostImageId());
                            imageResponse.setLostImagePath(image.getLostImagePath());
                            return imageResponse;
                        }).collect(Collectors.toList())
        );

        return response;
    }

    public List<LostReportsResponse> getLostReportByLostId(int lostId) {
        List<LostReports> lostReportsList = lostReportsRepository.findAll();

        return lostReportsList.stream()
                .filter(lostReport -> lostReport.getLostId() == lostId)
                .map(lostReport -> {
                    LostReportsResponse response = new LostReportsResponse();
                    response.setLostId(lostReport.getLostId());
                    response.setLostTitle(lostReport.getLostTitle());
                    response.setPetBreed(lostReport.getPetBreed());
                    response.setPetGender(lostReport.getPetGender());
                    response.setPetAge(lostReport.getPetAge());
                    response.setLostAreaLat(lostReport.getLostAreaLat());
                    response.setLostAreaLng(lostReport.getLostAreaLng());
                    response.setLostDate(lostReport.getLostDate());
                    response.setLostLocation(lostReport.getLostLocation());
                    response.setPetFeature(lostReport.getPetFeature());
                    response.setLostDescription(lostReport.getLostDescription());
                    response.setLostContact(lostReport.getLostContact());
                    response.setLostImages(
                            lostReport.getLostImages().stream()
                                    .map(image -> {
                                        LostReportsImageResponse imageResponse = new LostReportsImageResponse();
                                        imageResponse.setLostImageId(image.getLostImageId());
                                        imageResponse.setLostImagePath(image.getLostImagePath());
                                        return imageResponse;
                                    }).collect(Collectors.toList())
                    );
                    return response;
                }).collect(Collectors.toList());
    }

    public boolean updateLostReport(int lostId, LostReportsEditResponse lostReportsEditResponse) {
        Optional<LostReports> existingReport = lostReportsRepository.findById(lostId);
        if (existingReport.isPresent()) {
            LostReports report = existingReport.get();

            // DTO에서 데이터를 엔티티로 변환하여 업데이트
            report.setLostTitle(lostReportsEditResponse.getTitle());
            report.setLostDate(lostReportsEditResponse.getDate());
            report.setLostLocation(lostReportsEditResponse.getLocation());
            report.setPetFeature(lostReportsEditResponse.getPetFeature());
            report.setLostDescription(lostReportsEditResponse.getDescription());
            report.setLostContact(lostReportsEditResponse.getContact());
            report.setLostAreaLat(lostReportsEditResponse.getLatitude());
            report.setLostAreaLng(lostReportsEditResponse.getLongitude());

            // 데이터베이스에 저장
            lostReportsRepository.save(report);
            return true;
        }
        return false;
    }

    public void deleteLostById(int lostId) {
        // lostReportsImageRepository에서 lostId에 해당하는 이미지들을 찾음
        //List<LostReportsImage> images = lostReportsImageRepository.findByLostId(lostId);

        // GCS에서 파일 삭제
        //for (LostReportsImage image : images) {
        //String imagePath = image.getLostImagePath();
        //try {
        //    gcsUploaderService.deleteFile(imagePath);  // GCS에서 이미지 삭제
        //  System.out.println("이미지 삭제 완료");
        //} catch (IOException e) {
        //throw new RuntimeException("GCS에서 이미지 삭제 실패: " + imagePath, e);
        // }
        // }
        try {
            // lostId에 해당하는 LostReportsImage 데이터를 삭제
            List<LostReportsImage> lostReportsImages = lostReportsImageRepository.findByLostId(lostId);
            for (LostReportsImage image : lostReportsImages) {
                lostReportsImageRepository.delete(image);
            }

            // lostId에 해당하는 LostReports 데이터를 삭제
            lostReportsRepository.deleteById(lostId);

            System.out.println("데이터베이스에서 lostId " + lostId + "에 해당하는 보고서 및 이미지 삭제 완료");
        } catch (Exception e) {
            // 오류 처리
            e.printStackTrace();
            System.out.println("삭제 중 오류 발생: " + e.getMessage());
        }
    }

}
