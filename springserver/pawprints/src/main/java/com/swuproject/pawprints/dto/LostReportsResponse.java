package com.swuproject.pawprints.dto;

import com.swuproject.pawprints.domain.Pet;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;
import java.util.List;

public class LostReportsResponse {
    private int lostId;
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private int petId;
    private String userId;
    private String petBreed;
    private String petGender;
    private int petAge;
    private String petFeature;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;
    private List<LostReportsImageResponse> lostImages;


    public LostReportsResponse() {
        // 기본 생성자
    }

    public LostReportsResponse(int lostId, int petId, String lostTitle, Double lostAreaLat, Double lostAreaLng, Date lostDate, String lostLocation, String lostDescription, String lostContact, List<LostReportsImageResponse> lostImages) {
        this.lostId = lostId;
        this.petId = petId;
        this.lostTitle = lostTitle;
        this.lostAreaLat = lostAreaLat;
        this.lostAreaLng = lostAreaLng;
        this.lostDate = lostDate;
        this.lostLocation = lostLocation;
        this.lostDescription = lostDescription;
        this.lostContact = lostContact;
        this.lostImages = lostImages;
    }


    public int getLostId() {
        return lostId;
    }

    public void setLostId(int lostId) {
        this.lostId = lostId;
    }

    public int getPetId() { return petId; }

    public void setPetId(int petId) { this.petId = petId; }

    private Pet pet = new Pet();

    private PetResponse pet2;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public PetResponse getPetResponse() {
        return pet2;
    }

    public void setPetResponse(PetResponse pet2) {
        this.pet2 = pet2;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

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

    public String getPetFeature() { return petFeature; }

    public void setPetFeature(String petFeature) { this.petFeature = petFeature; }

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

    public String getLostContact() { return lostContact; }

    public void setLostContact(String lostContact) { this.lostContact = lostContact; }

    public List<LostReportsImageResponse> getLostImages() {
        return lostImages;
    }

    public void setLostImages(List<LostReportsImageResponse> lostImages) {
        this.lostImages = lostImages;
    }
}
