package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MatchDetailResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("utcDate")
    private String utcDate;

    @SerializedName("score")
    private Score score;

    @SerializedName("homeTeam")
    private TeamLineup homeTeam;

    @SerializedName("awayTeam")
    private TeamLineup awayTeam;

    @SerializedName("goals")
    private List<Goal> goals;

    @SerializedName("bookings")
    private List<Booking> bookings;

    // Getters
    public String getStatus() { return status; }
    public String getUtcDate() { return utcDate; }
    public Score getScore() { return score; }
    public TeamLineup getHomeTeam() { return homeTeam; }
    public TeamLineup getAwayTeam() { return awayTeam; }
    public List<Goal> getGoals() { return goals; }
    public List<Booking> getBookings() { return bookings; }


    /**
     * 試合に出場したチームのラインナップ情報を保持する内部クラス
     */
    public static class TeamLineup {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("crest")
        private String crest;

        // ▼▼▼ 監督の情報を追加 ▼▼▼
        @SerializedName("coach")
        private Player coach;

        @SerializedName("lineup")
        private List<Player> lineup;

        @SerializedName("bench")
        private List<Player> bench;

        // 各ゲッター
        public int getId() { return id; }
        public String getName() { return name; }
        public String getCrest() { return crest; }
        public Player getCoach() { return coach; } // ◀️ 監督情報のゲッター
        public List<Player> getLineup() { return lineup; }
        public List<Player> getBench() { return bench; }
    }
}