package com.swuproject.pawprints.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "lost_reports")
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "pet_id", nullable = false)
    private int petId;

    @Column(name = "lost_title")
    private String lostTitle;

    @Column(name = "lost_location")
    private String lostLocation;

    @Column(name = "lost_date")
    private String lostDate;

    @Column(name = "lost_description")
    private String lostDescription;

    @Column(name = "lost_contact")
    private String lostContact;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }

    public String getLostDate() {
        return lostDate;
    }

    public void setLostDate(String lostDate) {
        this.lostDate = lostDate;
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
