package com.swuproject.pawprints.dto;

import java.util.Date;
import java.util.List;

public class LostReportsImageResponse {
    private int lostImageId;
    private String lostImagePath;

    // Getters and Setters
    public int getLostImageId() { return lostImageId; }

    public void setLostImageId(int lostImageId) { this.lostImageId = lostImageId; }

    public String getLostImagePath() { return lostImagePath; }

    public void setLostImagePath(String lostImagePath) { this.lostImagePath = lostImagePath; }
}