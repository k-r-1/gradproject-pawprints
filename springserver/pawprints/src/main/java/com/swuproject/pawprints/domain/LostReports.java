package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostId;

    private Long petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    // 생성자

    public LostReports() {
    }

    public LostReports(Long petId, String lostTitle, Double lostAreaLat, Double lostAreaLng, Date lostDate, String lostLocation, String lostDescription, String lostContact) {
        this.petId = petId;
        this.lostTitle = lostTitle;
        this.lostAreaLat = lostAreaLat;
        this.lostAreaLng = lostAreaLng;
        this.lostDate = lostDate;
        this.lostLocation = lostLocation;
        this.lostDescription = lostDescription;
        this.lostContact = lostContact;
    }

    // Getter와 Setter

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
}
