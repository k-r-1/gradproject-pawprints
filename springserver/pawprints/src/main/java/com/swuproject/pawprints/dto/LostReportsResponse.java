package com.swuproject.pawprints.dto;

import java.util.Date;
import java.util.List;

public class LostReportsResponse {
    private int lostId;
    private int petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;
    private List<LostReportsImageResponse> lostImages;
    private PetResponse breed;
    private PetResponse gender;
    private PetResponse age;

    // Getters and Setters
    public int getLostId() {
        return lostId;
    }

    public void setLostId(int lostId) {
        this.lostId = lostId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
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

    public String getLostContact() {
        return lostContact;
    }

    public void setLostContact(String lostContact) {
        this.lostContact = lostContact;
    }

    public List<LostReportsImageResponse> getLostImages() {
        return lostImages;
    }

    public void setLostImages(List<LostReportsImageResponse> lostImages) {
        this.lostImages = lostImages;
    }

    public PetResponse getBreed() {
        return breed;
    }

    public void setBreed(PetResponse breed) {
        this.breed = breed;
    }

    public PetResponse getGender() {
        return gender;
    }

    public void setGender(PetResponse gender) {
        this.gender = gender;
    }

    public PetResponse getAge() {
        return age;
    }

    public void setAge(PetResponse age) {
        this.age = age;
    }
}
