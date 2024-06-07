// LostReportsImage.java
package com.swuproject.pawprints.domain;

import javax.persistence.*;

@Entity
@Table(name = "lost_reports_image")
public class LostReportsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostImageId;

    @ManyToOne
    @JoinColumn(name = "lost_id")
    private LostReports lostReport;

    private String lostImagePath;

    // Getters and Setters

    public Long getLostImageId() {
        return lostImageId;
    }

    public void setLostImageId(Long lostImageId) {
        this.lostImageId = lostImageId;
    }

    public LostReports getLostReport() {
        return lostReport;
    }

    public void setLostReport(LostReports lostReport) {
        this.lostReport = lostReport;
    }

    public String getLostImagePath() {
        return lostImagePath;
    }

    public void setLostImagePath(String lostImagePath) {
        this.lostImagePath = lostImagePath;
    }
}