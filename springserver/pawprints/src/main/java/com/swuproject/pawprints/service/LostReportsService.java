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
            PetResponse petResponse = new PetResponse();
            response.setLostId(lostReports.getLostId());
            response.setPetId(lostReports.getPetId());
            List<Pet> petList = petRepository.findLostPetsByPetId(lostReports.getPetId());
            response.setPetBreed(petList.get(0).getBreed());
            response.setPetGender(petList.get(0).getGender());
            response.setPetAge(petList.get(0).getAge());
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
}
