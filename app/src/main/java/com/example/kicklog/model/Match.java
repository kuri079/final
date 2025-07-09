// 新規作成: com/example/kicklog/model/Match.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("homeTeam")
    private Team homeTeam;

    @SerializedName("awayTeam")
    private Team awayTeam;

    @SerializedName("score")
    private Score score;

    @SerializedName("utcDate")
    private String utcDate;

    @SerializedName("status")
    private String status;

    // Getters
    public Team getHomeTeam() { return homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    public Score getScore() { return score; }
    public String getUtcDate() { return utcDate; }
    public String getStatus() { return status; }
}