package com.swuproject.pawprints.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lost_reports")
public class LostReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostId;

    private String lostTitle;
    private Double lostAreaLat;
    private Double lostAreaLng;
    private Date lostDate;
    private String lostLocation;
    private String lostDescription;
    private String lostContact;

    // Getters and Setters
}
