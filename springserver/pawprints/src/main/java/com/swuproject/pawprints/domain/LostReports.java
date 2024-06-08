package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lostId;

    private int petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    @OneToMany(mappedBy = "lostReports", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LostReportsImage> lostImages;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "breed_id")
    private Pet breed;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "gender_id")
    private Pet gender;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "age_id")
    private Pet age;

    // Getters and setters
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
    public Double getLostAreaLat() { return lostAreaLat; }
    public void setLostAreaLat(Double lostAreaLat) { this.lostAreaLat = lostAreaLat; }
    public Double getLostAreaLng() { return lostAreaLng; }
    public void setLostAreaLng(Double lostAreaLng) { this.lostAreaLng = lostAreaLng; }
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

    public List<LostReportsImage> getLostImages() { return lostImages; }
    public void setLostImages(List<LostReportsImage> lostImages) { this.lostImages = lostImages; }

    public Pet getBreed() { return breed; }
    public void setBreed(Pet breed) { this.breed = breed; }

    public Pet getGender() { return gender; }
    public void setGender(Pet gender) { this.gender = gender; }

    public Pet getAge() { return age; }
    public void setAge(Pet age) { this.age = age; }
}
