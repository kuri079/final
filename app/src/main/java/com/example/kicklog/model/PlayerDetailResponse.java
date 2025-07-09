package com.example.kicklog.model;
public class PlayerDetailResponse {
    private int id;
    private String name;
    private String nationality;
    private String position;
    private String dateOfBirth;
    private Team currentTeam;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPosition() {
        return position;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }
}
