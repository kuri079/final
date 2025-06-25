package com.example.kicklog.model;

import java.util.List;

public class TeamResponse {
    public List<TeamData> response;

    public List<TeamData> getResponse() {
        return response;
    }

    public static class TeamData {
        public Team team;
    }

    public static class Team {
        public int id;
        public String name;
        public String country;
        public String logo;
    }
}

