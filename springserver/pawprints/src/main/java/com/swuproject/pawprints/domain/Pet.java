package com.swuproject.pawprints.domain;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LostReports> lostReports;

    // Pet
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "pet_name", nullable = false)
    private String name;

    @Column(name = "pet_type", nullable = false)
    private String type;

    @Column(name = "pet_breed", nullable = false)
    private String breed;

    @Column(name = "pet_age", nullable = false)
    private int age;

    @Column(name = "pet_gender", nullable = false)
    private String gender;

    @Column(name = "pet_color", nullable = false)
    private String color;

    @Column(name = "pet_feature", nullable = false)
    private String feature;


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

}