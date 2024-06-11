package com.swuproject.pawprints.dto;

import com.swuproject.pawprints.domain.Pet;

import java.util.Date;
import java.util.List;

public class LostReportsResponse {
    private int lostId;
    private String petBreed;
    private String petGender;
    private int petAge;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private List<LostReportsImageResponse> lostImages;


    public int getLostId() {
        return lostId;
    }

    public void setLostId(int lostId) {
        this.lostId = lostId;
    }

    private Pet pet = new Pet();

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public int getPetAge() {
        return petAge;
    }

    public void setPetAge(int petAge) {
        this.petAge = petAge;
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

    public Date getLostDate() {
        return lostDate;
    }

    public void setLostDate(Date lostDate) {
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

    public List<LostReportsImageResponse> getLostImages() {
        return lostImages;
    }

    public void setLostImages(List<LostReportsImageResponse> lostImages) {
        this.lostImages = lostImages;
    }
}
