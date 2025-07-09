package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScorersResponse {
    @SerializedName("scorers")
    private List<Scorer> scorers;

    public List<Scorer> getScorers() {
        return scorers;
    }
}