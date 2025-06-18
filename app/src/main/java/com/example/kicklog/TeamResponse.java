package com.example.kicklog;

import java.util.List;

public class TeamResponse {
    private String name;
    private List<SquadMember> squad;

    public String getName() { return name; }
    public List<SquadMember> getSquad() { return squad; }
}

