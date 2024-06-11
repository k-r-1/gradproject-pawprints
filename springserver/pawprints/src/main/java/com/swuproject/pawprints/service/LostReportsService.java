package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.Pet;
import com.swuproject.pawprints.dto.LostReportsImageResponse;
import com.swuproject.pawprints.dto.LostReportsResponse;
import com.swuproject.pawprints.dto.PetResponse;
import com.swuproject.pawprints.repository.LostReportsRepository;
import com.swuproject.pawprints.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostReportsService {

    @Autowired
    private LostReportsRepository lostReportsRepository;

    @Autowired
    private PetRepository petRepository;

    public List<LostReportsResponse> getAllLostReports() {
        List<LostReports> lostReportsList = lostReportsRepository.findAll();

        return lostReportsList.stream().map(lostReports -> {
            LostReportsResponse response = new LostReportsResponse();
            response.setLostId(lostReports.getLostId());
            response.setPet(lostReports.getPet());
            response.setLostTitle(lostReports.getLostTitle());
            response.setLostAreaLat(lostReports.getLostAreaLat());
            response.setLostAreaLng(lostReports.getLostAreaLng());
            response.setLostDate(lostReports.getLostDate());
            response.setLostLocation(lostReports.getLostLocation());
            response.setLostDescription(lostReports.getLostDescription());
            response.setLostImages(
                    lostReports.getLostImages().stream()
                            .map(image -> {
                                LostReportsImageResponse imageResponse = new LostReportsImageResponse();
                                imageResponse.setLostImageId(image.getLostImageId());
                                imageResponse.setLostImagePath(image.getLostImagePath());
                                return imageResponse;
                            }).collect(Collectors.toList())
            );

            // Add pet's information
            Pet pet = lostReports.getPet();
            PetResponse petResponse = new PetResponse();
            if (pet != null) {
                petResponse.setBreed(pet.getBreed() != null ? pet.getBreed() : "정보 없음");
                petResponse.setGender(pet.getGender() != null ? pet.getGender() : "정보 없음");
                petResponse.setAge(pet.getAge() != 0 ? pet.getAge() : 0); // 나이는 0일 수 있습니다.
            } else {
                petResponse.setBreed("정보 없음");
                petResponse.setGender("정보 없음");
                petResponse.setAge(0);
            }

            response.setPetResponse(petResponse);

            return response;
        }).collect(Collectors.toList());
    }
}
