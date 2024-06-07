package com.swuproject.pawprints.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "lost_reports_images")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "lostImageId")
public class LostReportsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_image_id")
    private Long lostImageId;

    @ManyToOne
    @JoinColumn(name = "lost_id", nullable = false)
    @JsonBackReference
    private LostReports lostReports;

    @Column(name = "lost_image_path", nullable = false)
    private String lostImagePath;

    // Getters and Setters

    public Long getLostImageId() {
        return lostImageId;
    }

    public void setLostImageId(Long lostImageId) {
        this.lostImageId = lostImageId;
    }

    public LostReports getLostReports() {
        return lostReports;
    }

    public void setLostReports(LostReports lostReports) {
        this.lostReports = lostReports;
    }

    public String getLostImagePath() {
        return lostImagePath;
    }

    public void setLostImagePath(String lostImagePath) {
        this.lostImagePath = lostImagePath;
    }
}
