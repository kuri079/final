package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Scorer {
    @SerializedName("player")
    private Player player;
    @SerializedName("team")
    private Team team;
    @SerializedName("goals")
    private Integer goals; // ★★★ここを 'int' から 'Integer' に変更しました ★★★

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    public Integer getGoals() { // ★★★ getterの戻り値も 'Integer' に変更しました ★★★
        return goals;
    }
}