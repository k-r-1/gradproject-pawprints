package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class SightReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sightId;

    private String userId;
    private String sightTitle;
    private String sightType;
    private String sightBreed;
    private Double sightAreaLat;
    private Double sightAreaLng;
    private Date sightDate;
    private String sightLocation;
    private String sightDescription;
    private String sightContact;

    @OneToMany(mappedBy = "sightReports", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SightReportsImage> sightImages;

    // Getters and Setters
    public int getSightId() { return sightId; }
    public void setSightId(int sightId) { this.sightId = sightId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSightTitle() { return sightTitle; }
    public void setSightTitle(String sightTitle) { this.sightTitle = sightTitle; }
    public String getSightType() { return sightType; }
    public void setSightType(String sightType) { this.sightType = sightType; }
    public String getSightBreed() { return sightBreed; }
    public void setSightBreed(String sightBreed) { this.sightBreed = sightBreed; }
    public Double getSightAreaLat() { return sightAreaLat; }
    public void setSightAreaLat(Double sightAreaLat) { this.sightAreaLat = sightAreaLat; }
    public Double getSightAreaLng() { return sightAreaLng; }
    public void setSightAreaLng(Double sightAreaLng) { this.sightAreaLng = sightAreaLng; }
    public Date getSightDate() { return sightDate; }
    public void setSightDate(Date sightDate) { this.sightDate = sightDate; }
    public String getSightLocation() { return sightLocation; }
    public void setSightLocation(String sightLocation) { this.sightLocation = sightLocation; }
    public String getSightDescription() { return sightDescription; }
    public void setSightDescription(String sightDescription) { this.sightDescription = sightDescription; }
    public String getSightContact() { return sightContact; }
    public void setSightContact(String sightContact) { this.sightContact = sightContact; }
    public List<SightReportsImage> getSightImages() { return sightImages; }
    public void setSightImages(List<SightReportsImage> sightImages) { this.sightImages = sightImages; }
}
