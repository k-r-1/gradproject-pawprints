package com.swuproject.pawprints.domain;

import jakarta.persistence.*;

@Entity
public class LostReportsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lostImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lostId", nullable = false)
    private LostReports lostReports;

    private String lostImagePath;

    // Getters and Setters
    public int getLostImageId() { return lostImageId; }
    public void setLostImageId(int lostImageId) { this.lostImageId = lostImageId; }
    public LostReports getLostReports() { return lostReports; }
    public void setLostReports(LostReports lostReports) { this.lostReports = lostReports; }
    public String getLostImagePath() { return lostImagePath; }
    public void setLostImagePath(String lostImagePath) { this.lostImagePath = lostImagePath; }
}