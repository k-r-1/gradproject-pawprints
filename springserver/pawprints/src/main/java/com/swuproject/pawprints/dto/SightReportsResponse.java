package com.swuproject.pawprints.dto;

import java.util.Date;
import java.util.List;

public class SightReportsResponse {
    private int sightId;
    private String sightTitle;
    private String sightBreed;
    private Double sightAreaLat;
    private Double sightAreaLng;
    private Date sightDate;
    private String sightLocation;
    private String sightDescription;
    private List<SightReportsImageResponse> sightImages;

    // Getters and Setters

    public int getSightId() {
        return sightId;
    }

    public void setSightId(int sightId) {
        this.sightId = sightId;
    }

    public String getSightTitle() {
        return sightTitle;
    }

    public void setSightTitle(String sightTitle) {
        this.sightTitle = sightTitle;
    }

    public String getSightBreed() {
        return sightBreed;
    }

    public void setSightBreed(String sightBreed) {
        this.sightBreed = sightBreed;
    }

    public Double getSightAreaLat() {
        return sightAreaLat;
    }

    public void setSightAreaLat(Double sightAreaLat) {
        this.sightAreaLat = sightAreaLat;
    }

    public Double getSightAreaLng() {
        return sightAreaLng;
    }

    public void setSightAreaLng(Double sightAreaLng) {
        this.sightAreaLng = sightAreaLng;
    }

    public Date getSightDate() {
        return sightDate;
    }

    public void setSightDate(Date sightDate) {
        this.sightDate = sightDate;
    }

    public String getSightLocation() {
        return sightLocation;
    }

    public void setSightLocation(String sightLocation) {
        this.sightLocation = sightLocation;
    }

    public String getSightDescription() {
        return sightDescription;
    }

    public void setSightDescription(String sightDescription) {
        this.sightDescription = sightDescription;
    }

    public List<SightReportsImageResponse> getSightImages() {
        return sightImages;
    }

    public void setSightImages(List<SightReportsImageResponse> sightImages) {
        this.sightImages = sightImages;
    }
}

