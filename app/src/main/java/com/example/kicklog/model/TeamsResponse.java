// 新規作成: com/example/kicklog/model/TeamsResponse.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TeamsResponse {

    @SerializedName("teams")
    private List<Team> teams;

    public List<Team> getTeams() {
        return teams;
    }
}