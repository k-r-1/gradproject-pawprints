package com.swuproject.pawprints.dto;

import java.util.Date;

public class LostReportsEditResponse {
    private String title;
    private Date date;
    private String location;
    private String feature;
    private String description;
    private String contact;  // 새로 추가된 필드
    private double latitude; // 새로 추가된 필드
    private double longitude; // 새로 추가된 필드

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPetFeature() {
        return feature;
    }

    public void setPetFeature(String feature) {
        this.feature = feature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
