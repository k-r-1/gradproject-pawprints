// LostReports.java
package com.swuproject.pawprints.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lost_reports")
public class LostReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostId;

    private Long petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private LocalDateTime lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    @OneToMany(mappedBy = "lostReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LostReportsImage> lostImages;

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

    public List<LostReportsImage> getLostImages() {
        return lostImages;
    }

    public void setLostImages(List<LostReportsImage> lostImages) {
        this.lostImages = lostImages;
    }
}