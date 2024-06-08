package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostId;

    private Long petId;
    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private String lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    @OneToMany(mappedBy = "lostReports", cascade = CascadeType.ALL)
    private List<LostReportsImage> images;

    // Getters and Setters
    public Long getLostId() { return lostId; }
    public void setLostId(Long lostId) { this.lostId = lostId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public String getLostTitle() { return lostTitle; }
    public void setLostTitle(String lostTitle) { this.lostTitle = lostTitle; }

    public Double getLostAreaLat() { return lostAreaLat; }
    public void setLostAreaLat(Double lostAreaLat) { this.lostAreaLat = lostAreaLat; }

    public Double getLostAreaLng() { return lostAreaLng; }
    public void setLostAreaLng(Double lostAreaLng) { this.lostAreaLng = lostAreaLng; }

    public String getLostDate() { return lostDate; }
    public void setLostDate(String lostDate) { this.lostDate = lostDate; }

    public String getLostLocation() { return lostLocation; }
    public void setLostLocation(String lostLocation) { this.lostLocation = lostLocation; }

    public String getLostDescription() { return lostDescription; }
    public void setLostDescription(String lostDescription) { this.lostDescription = lostDescription; }

    public String getLostContact() { return lostContact; }
    public void setLostContact(String lostContact) { this.lostContact = lostContact; }

    public List<LostReportsImage> getImages() { return images; }
    public void setImages(List<LostReportsImage> images) { this.images = images; }
}
