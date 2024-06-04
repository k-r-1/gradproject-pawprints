package com.swuproject.pawprints.domain;

import javax.persistence.*;

@Entity
@Table(name = "lost_reports_image")
public class LostReportsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostImageId;

    private Long lostId;
    private String lostImagePath;

    // Getters and Setters
}
