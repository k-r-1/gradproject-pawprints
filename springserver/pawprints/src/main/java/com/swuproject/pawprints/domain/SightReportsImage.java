package com.swuproject.pawprints.domain;

import jakarta.persistence.*;
import com.swuproject.pawprints.domain.SightReports;

@Entity
public class SightReportsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sightImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sightId", nullable = false)
    private SightReports sightReports;

    private String sightImagePath;

    // Getters and Setters
    public int getSightImageId() { return sightImageId; }
    public void setSightImageId(int sightImageId) { this.sightImageId = sightImageId; }
    public SightReports getSightReports() { return sightReports; }
    public void setSightReports(SightReports sightReports) { this.sightReports = sightReports; }
    public String getSightImagePath() { return sightImagePath; }
    public void setSightImagePath(String sightImagePath) { this.sightImagePath = sightImagePath; }
}
