package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lostId;

    @ManyToOne

    private Pet pet; // pet 필드를 사용하여 Pet 엔티티를 참조하도록 수정

    @JoinColumn(name = "pet_id")
    private int petId;

    // Pet 엔티티의 필드를 가져올 수 있도록 추가
    @Transient // 데이터베이스에 매핑하지 않음
    private String petBreed;

    @Transient
    private String petGender;

    @Transient
    private int petAge;

    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    @OneToMany(mappedBy = "lostReports", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LostReportsImage> lostImages;

    // Getters and Setters

    public int getLostId() {
        return lostId;
    }

    public void setLostId(int lostId) {
        this.lostId = lostId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        // Pet 엔티티가 설정되면 해당 정보를 가져와서 설정
        this.petBreed = pet.getBreed();
        this.petGender = pet.getGender();
        this.petAge = pet.getAge();
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
}

