package com.example.kicklog.model;

import java.util.List;

public class StandingResponse {
    public List<Table> standings;   // 実際は配列 1 個

    public static class Table {
        public String type;               // "TOTAL"
        public List<Entry> table;
    }
    public static class Entry {
        public Team team;
        public int playedGames, won, draw, lost;
        public int goalsFor, goalsAgainst, goalDifference;
        public int points;
    }
    public static class Team {
        public int id;
        public String name, crest;
    }
}
