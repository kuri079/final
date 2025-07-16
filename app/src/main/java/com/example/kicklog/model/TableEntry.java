package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class TableEntry {
    @SerializedName("position")
    private Integer position;
    @SerializedName("team")
    private Team team;
    @SerializedName("playedGames")
    private Integer playedGames;
    @SerializedName("won")
    private Integer won;
    @SerializedName("draw")
    private Integer draw;
    @SerializedName("lost")
    private Integer lost;
    @SerializedName("goalsFor")
    private Integer goalsFor;
    @SerializedName("goalsAgainst")
    private Integer goalsAgainst;
    @SerializedName("goalDifference")
    private Integer goalDifference;
    @SerializedName("points")
    private Integer points;

    public Integer getPosition() {
        return position;
    }

    public Team getTeam() {
        return team;
    }

    public Integer getPlayedGames() {
        return playedGames;
    }

    public Integer getWon() {
        return won;
    }

    public Integer getDraw() {
        return draw;
    }

    public Integer getLost() {
        return lost;
    }

    public Integer getGoalsFor() {
        return goalsFor;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public Integer getPoints() {
        return points;
    }
}