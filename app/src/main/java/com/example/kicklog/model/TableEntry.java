// 新規作成: com/example/kicklog/model/TableEntry.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class TableEntry {
    @SerializedName("position")
    private int position;
    @SerializedName("team")
    private Team team;
    @SerializedName("playedGames")
    private int playedGames;
    @SerializedName("won")
    private int won;
    @SerializedName("draw")
    private int draw;
    @SerializedName("lost")
    private int lost;
    @SerializedName("points")
    private int points;
    @SerializedName("goalsFor")
    private int goalsFor;
    @SerializedName("goalsAgainst")
    private int goalsAgainst;

    // 各ゲッターを追加
    public int getPosition() { return position; }
    public Team getTeam() { return team; }
    public int getPlayedGames() { return playedGames; }
    public int getWon() { return won; }
    public int getDraw() { return draw; }
    public int getLost() { return lost; }
    public int getPoints() { return points; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
}