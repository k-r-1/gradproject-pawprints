package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.dto.LostReportsImageResponse;
import com.swuproject.pawprints.dto.LostReportsResponse;
import com.swuproject.pawprints.dto.PetResponse;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostReportsService {

    @Autowired
    private LostReportsRepository lostReportsRepository;

    public List<LostReportsResponse> getAllLostReports() {
        List<LostReports> lostReportsList = lostReportsRepository.findAll();

        return lostReportsList.stream().map(lostReport -> {
            LostReportsResponse response = new LostReportsResponse();
            response.setLostId(lostReport.getLostId());
            response.setLostTitle(lostReport.getLostTitle());
            response.setLostAreaLat(lostReport.getLostAreaLat());
            response.setLostAreaLng(lostReport.getLostAreaLng());
            response.setLostDate(lostReport.getLostDate());
            response.setLostLocation(lostReport.getLostLocation());
            response.setLostDescription(lostReport.getLostDescription());
            response.setLostImages(
                    lostReport.getLostImages().stream()
                            .map(image -> {
                                LostReportsImageResponse imageResponse = new LostReportsImageResponse();
                                imageResponse.setLostImageId(image.getLostImageId());
                                imageResponse.setLostImagePath(image.getLostImagePath());
                                return imageResponse;
                            }).collect(Collectors.toList())
            );

            // Breed
            PetResponse breedResponse = new PetResponse();
            breedResponse.setBreed(lostReport.getBreed().getBreed());
            response.setBreed(breedResponse);

            // Gender
            PetResponse genderResponse = new PetResponse();
            genderResponse.setGender(lostReport.getGender().getGender());
            response.setGender(genderResponse);

            // Age
            PetResponse ageResponse = new PetResponse();
            ageResponse.setAge(lostReport.getAge().getAge());
            response.setAge(ageResponse);

            return response;
        }).collect(Collectors.toList());
    }
}
