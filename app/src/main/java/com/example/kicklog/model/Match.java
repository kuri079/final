package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("id")
    private int id;
    @SerializedName("utcDate")
    private String utcDate;
    @SerializedName("status")
    private String status;
    @SerializedName("matchday")
    private Integer matchday;
    @SerializedName("homeTeam")
    private Team homeTeam;
    @SerializedName("awayTeam")
    private Team awayTeam;
    @SerializedName("score")
    private Score score;
    @SerializedName("competition")
    private Competition competition; // ★確認または追加

    public int getId() {
        return id;
    }

    public String getUtcDate() {
        return utcDate;
    }

    public String getStatus() {
        return status;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Score getScore() {
        return score;
    }

    public Competition getCompetition() { // ★確認または追加
        return competition;
    }
    // Setters if needed
}