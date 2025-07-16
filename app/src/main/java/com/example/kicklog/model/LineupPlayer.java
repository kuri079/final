package com.example.kicklog.model;

public class LineupPlayer {
    private String positionType; // "GK", "DF", "MF", "FW", "CMF"など
    private String name;
    private int shirtNumber;

    public LineupPlayer(String positionType, String name, int shirtNumber) {
        this.positionType = positionType;
        this.name = name;
        this.shirtNumber = shirtNumber;
    }

    public String getPositionType() {
        return positionType;
    }

    public String getName() {
        return name;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }
}