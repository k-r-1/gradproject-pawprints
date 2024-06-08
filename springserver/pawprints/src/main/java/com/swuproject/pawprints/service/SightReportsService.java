package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.SightReports;
import com.swuproject.pawprints.dto.SightReportsImageResponse;
import com.swuproject.pawprints.dto.SightReportsResponse;
import com.swuproject.pawprints.repository.SightReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SightReportsService {

    @Autowired
    private SightReportsRepository sightReportsRepository;

    public List<SightReportsResponse> getAllSightReports() {
        List<SightReports> sightReportsList = sightReportsRepository.findAll();

        return sightReportsList.stream().map(sightReport -> {
            SightReportsResponse response = new SightReportsResponse();
            response.setSightId(sightReport.getSightId());
            response.setSightTitle(sightReport.getSightTitle());
            response.setSightBreed(sightReport.getSightBreed());
            response.setSightAreaLat(sightReport.getSightAreaLat());
            response.setSightAreaLng(sightReport.getSightAreaLng());
            response.setSightDate(sightReport.getSightDate());
            response.setSightLocation(sightReport.getSightLocation());
            response.setSightDescription(sightReport.getSightDescription());
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
}
