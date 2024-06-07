package com.swuproject.pawprints.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;

@Entity
@Table(name = "lost_reports")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "lostId")
public class LostReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_id")
    private Long lostId;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference
    private Pet pet;

    @Column(name = "lost_title", nullable = false)
    private String lostTitle;

    @Column(name = "lost_area_lat", nullable = false)
    private Double lostAreaLat;

    @Column(name = "lost_area_lng", nullable = false)
    private Double lostAreaLng;

    @Column(name = "lost_date", nullable = false)
    private String lostDate;

    @Column(name = "lost_location", nullable = false)
    private String lostLocation;

    @Column(name = "lost_description", nullable = false)
    private String lostDescription;

    @Column(name = "lost_contact", nullable = false)
    private String lostContact;

    @OneToMany(mappedBy = "lostReports", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<LostReportsImage> images;

    // Getters and Setters

    public Long getLostId() {
        return lostId;
    }

    public void setLostId(Long lostId) {
        this.lostId = lostId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
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

    public String getLostDate() {
        return lostDate;
    }

    public void setLostDate(String lostDate) {
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

    public List<LostReportsImage> getImages() {
        return images;
    }

    public void setImages(List<LostReportsImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "LostReports{" +
                "lostId=" + lostId +
                ", lostTitle='" + lostTitle + '\'' +
                ", lostAreaLat=" + lostAreaLat +
                ", lostAreaLng=" + lostAreaLng +
                ", lostDate='" + lostDate + '\'' +
                ", lostLocation='" + lostLocation + '\'' +
                ", lostDescription='" + lostDescription + '\'' +
                ", lostContact='" + lostContact + '\'' +
                ", pet=" + (pet != null ? pet.getId() : null) +
                '}';
    }
}


