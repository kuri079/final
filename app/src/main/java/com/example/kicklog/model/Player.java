package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Player {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("dateOfBirth")
    private String dateOfBirth; // String型で受け取ることが多い
    @SerializedName("nationality")
    private String nationality; // ★追加
    @SerializedName("position")
    private String position; // ★追加
    @SerializedName("shirtNumber")
    private Integer shirtNumber; // ★追加 (APIによってはnullの場合があるためInteger)
    @SerializedName("role")
    private String role; // サブで役割がある場合（もしあれば）

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() { // ★追加
        return nationality;
    }

    public String getPosition() { // ★追加
        return position;
    }

    public Integer getShirtNumber() { // ★追加
        return shirtNumber;
    }

    public String getRole() {
        return role;
    }
}