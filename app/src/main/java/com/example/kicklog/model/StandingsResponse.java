// 新規作成: com/example/kicklog/model/StandingsResponse.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StandingsResponse {
    @SerializedName("standings")
    private List<Standing> standings;

    public List<Standing> getStandings() { return standings; }
}