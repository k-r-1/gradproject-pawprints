package com.swuproject.pawprints.domain;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class LostReportsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostImageId;

    private String lostImagePath;

    @ManyToOne
    @JoinColumn(name = "lost_id")
    @JsonBackReference
    private LostReports lostReports;

    // Getters and Setters
    public Long getLostImageId() {
        return lostImageId;
    }

    public void setLostImageId(Long lostImageId) {
        this.lostImageId = lostImageId;
    }

    public String getLostImagePath() {
        return lostImagePath;
    }

    public void setLostImagePath(String lostImagePath) {
        this.lostImagePath = lostImagePath;
    }

    public LostReports getLostReports() {
        return lostReports;
    }

    public void setLostReports(LostReports lostReports) {
        this.lostReports = lostReports;
    }
}
