package com.example.kicklog.model;

import java.util.List;

public class TeamResponse {
    public List<Team> teams;      // ← ★ 内部 Team は削除してこれだけ
    public List<Squad> squad;     // 選手用

    public static class Squad {
        public int id;
        public String name;
        public String position;
        public String nationality;
    }
}

