package com.swuproject.pawprints.dto;

import java.util.Date;
import java.util.List;

public class SightReportsImageResponse {
    private int sightImageId;
    private String sightImagePath;

    // Getters and Setters

    public int getSightImageId() {
        return sightImageId;
    }

    public void setSightImageId(int sightImageId) {
        this.sightImageId = sightImageId;
    }

    public String getSightImagePath() {
        return sightImagePath;
    }

    public void setSightImagePath(String sightImagePath) {
        this.sightImagePath = sightImagePath;
    }
}

