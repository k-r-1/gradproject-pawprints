package com.swuproject.pawprints.service;

import com.swuproject.pawprints.dto.LostReportsDTO;
import com.swuproject.pawprints.dto.LostReportsImageDTO;
import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.domain.LostReportsImage;
import com.swuproject.pawprints.domain.Pet;
import com.swuproject.pawprints.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostReportsService {

    @Autowired
    private PetRepository petRepository;

    public LostReportsDTO convertToDTO(LostReports lostReports) {
        LostReportsDTO dto = new LostReportsDTO();
        dto.setLostId(lostReports.getLostId());
        dto.setPetId(lostReports.getPetId());
        dto.setLostTitle(lostReports.getLostTitle());
        dto.setLostAreaLat(lostReports.getLostAreaLat());
        dto.setLostAreaLng(lostReports.getLostAreaLng());
        dto.setLostDate(lostReports.getLostDate());
        dto.setLostLocation(lostReports.getLostLocation());
        dto.setLostDescription(lostReports.getLostDescription());
        dto.setLostContact(lostReports.getLostContact());
        dto.setImages(lostReports.getImages().stream()
                .map(this::convertImageToDTO)
                .collect(Collectors.toList()));

        Pet pet = petRepository.findById(lostReports.getPetId()).orElse(null);
        if (pet != null) {
            dto.setLostBreed(pet.getBreed());
            dto.setLostGender(pet.getGender());
        }
        return dto;
    }

    private LostReportsImageDTO convertImageToDTO(LostReportsImage image) {
        LostReportsImageDTO dto = new LostReportsImageDTO();
        dto.setLostImageId(image.getLostImageId());
        dto.setLostImagePath(image.getLostImagePath());
        return dto;
    }
}
