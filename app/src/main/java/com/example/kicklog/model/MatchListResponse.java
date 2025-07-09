// 新規作成: com/example/kicklog/model/MatchListResponse.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MatchListResponse {
    @SerializedName("matches")
    private List<Match> matches;

    public List<Match> getMatches() {
        return matches;
    }
}