package com.swuproject.pawprints.dto;

public class LostReportsImageDTO {
    private Long lostImageId;
    private String lostImagePath;

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
}
