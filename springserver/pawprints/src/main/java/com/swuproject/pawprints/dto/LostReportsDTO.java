package com.swuproject.pawprints.dto;

import java.time.LocalDateTime;
import java.util.List;

public class LostReportsDTO {
    private Long lostId;
    private Long petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private LocalDateTime lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;
    private List<LostReportsImageDTO> images;
    private String lostBreed;
    private String lostGender;

    // Getters and Setters
    public Long getLostId() {
        return lostId;
    }

    public void setLostId(Long lostId) {
        this.lostId = lostId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getLostTitle() {
        return lostTitle;
    }

    public void setLostTitle(String lostTitle) {
        this.lostTitle = lostTitle;
    }

    public Double getLostAreaLat() {
        return lostAreaLat;
    }

    public void setLostAreaLat(Double lostAreaLat) {
        this.lostAreaLat = lostAreaLat;
    }

    public Double getLostAreaLng() {
        return lostAreaLng;
    }

    public void setLostAreaLng(Double lostAreaLng) {
        this.lostAreaLng = lostAreaLng;
    }

    public LocalDateTime getLostDate() {
        return lostDate;
    }

    public void setLostDate(LocalDateTime lostDate) {
        this.lostDate = lostDate;
    }

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }

    public String getLostDescription() {
        return lostDescription;
    }

    public void setLostDescription(String lostDescription) {
        this.lostDescription = lostDescription;
    }

    public String getLostContact() {
        return lostContact;
    }

    public void setLostContact(String lostContact) {
        this.lostContact = lostContact;
    }

    public List<LostReportsImageDTO> getImages() {
        return images;
    }

    public void setImages(List<LostReportsImageDTO> images) {
        this.images = images;
    }

    public String getLostBreed() {
        return lostBreed;
    }

    public void setLostBreed(String lostBreed) {
        this.lostBreed = lostBreed;
    }

    public String getLostGender() {
        return lostGender;
    }

    public void setLostGender(String lostGender) {
        this.lostGender = lostGender;
    }
}
