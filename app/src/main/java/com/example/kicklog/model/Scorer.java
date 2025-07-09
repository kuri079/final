package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Scorer {
    @SerializedName("player")
    private Player player;

    @SerializedName("team")
    private Team team;

    @SerializedName("goals")
    private int goals;

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    public int getGoals() {
        return goals;
    }
}