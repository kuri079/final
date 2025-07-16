// 新規作成: com/example/kicklog/model/Goal.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Goal {

    @SerializedName("minute")
    private int minute;

    @SerializedName("scorer")
    private Player scorer;

    @SerializedName("team")
    private Team team;

    // 各ゲッター
    public int getMinute() {
        return minute;
    }

    public Player getScorer() {
        return scorer;
    }

    public Team getTeam() {
        return team;
    }
}