// app/src/main/java/com/example/kicklog/model/MatchResponse.java
package com.example.kicklog.model;

import java.util.List;

public class MatchResponse {
    public List<Match> matches;

    public static class Match {
        public int    id;
        public String utcDate;          // ISO 8601
        public String status;           // FINISHED / SCHEDULED …
        public Score  score;
        public Team   homeTeam;
        public Team   awayTeam;
    }

    public static class Score {
        public FullTime fullTime;
        public ExtraTime extraTime;
        public Penalties penalties;
    }
    public static class FullTime { public Integer home; public Integer away; }
    public static class ExtraTime{ public Integer home; public Integer away; }
    public static class Penalties{ public Integer home; public Integer away; }

    public static class Team {
        public int    id;
        public String name;
        public String shortName;
        public String crest;    // URL
    }
}

