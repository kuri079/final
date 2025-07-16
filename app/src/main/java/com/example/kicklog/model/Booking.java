// 新規作成: com/example/kicklog/model/Booking.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("minute")
    private int minute;

    @SerializedName("team")
    private Team team;

    @SerializedName("player")
    private Player player;

    @SerializedName("card")
    private String card; // "YELLOW_CARD" または "RED_CARD"

    // 各ゲッター
    public int getMinute() {
        return minute;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCard() {
        return card;
    }
}