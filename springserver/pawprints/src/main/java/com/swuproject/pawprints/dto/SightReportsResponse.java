package com.swuproject.pawprints.dto;

import java.util.Date;
import java.util.List;

public class SightReportsResponse {
    private int sightId;
    private String userId;
    private String sightTitle;
    private String sightBreed;
    private Double sightAreaLat;
    private Double sightAreaLng;
    private Date sightDate;
    private String sightLocation;
    private String sightDescription;
    private String sightContact;
    private List<SightReportsImageResponse> sightImages;

    public SightReportsResponse() {
        // 기본 생성자
    }

    public SightReportsResponse(int sightId, String userId, String sightTitle, String sightBreed, Double sightAreaLat, Double sightAreaLng, Date sightDate, String sightLocation, String sightDescription, String sightContact,List<SightReportsImageResponse> sightImages) {
        this.sightId = sightId;
        this.userId = userId;
        this.sightTitle = sightTitle;
        this.sightBreed = sightBreed;
        this.sightAreaLat = sightAreaLat;
        this.sightAreaLng = sightAreaLng;
        this.sightDate = sightDate;
        this.sightLocation = sightLocation;
        this.sightDescription = sightDescription;
        this.sightContact = sightContact;
        this.sightImages = sightImages;
    }

    // Getters and Setters

    public int getSightId() {
        return sightId;
    }

    public void setSightId(int sightId) {
        this.sightId = sightId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) { this.userId = userId; }
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

    public String getSightContact() {
        return sightContact;
    }

    public void setSightContact(String sightContact) {
        this.sightContact = sightContact;
    }

    public List<SightReportsImageResponse> getSightImages() {
        return sightImages;
    }

    public void setSightImages(List<SightReportsImageResponse> sightImages) {
        this.sightImages = sightImages;
    }
}

