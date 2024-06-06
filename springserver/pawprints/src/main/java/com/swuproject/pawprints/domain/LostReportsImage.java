package com.swuproject.pawprints.domain;

import jakarta.persistence.*;

@Entity
public class LostReportsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lostImageId;
    private Long lostId;
    private String lostImagePath;

    // 기본 생성자
    public LostReportsImage() {}

    // 모든 필드를 포함한 생성자
    public LostReportsImage(Long lostId, String lostImagePath) {
        this.lostId = lostId;
        this.lostImagePath = lostImagePath;
    }

    // Getter와 Setter
    public Long getLostImageId() {
        return lostImageId;
    }

    public void setLostImageId(Long lostImageId) {
        this.lostImageId = lostImageId;
    }

    public Long getLostId() {
        return lostId;
    }

    public void setLostId(Long lostId) {
        this.lostId = lostId;
    }

    public String getLostImagePath() {
        return lostImagePath;
    }

    public void setLostImagePath(String lostImagePath) {
        this.lostImagePath = lostImagePath;
    }
}