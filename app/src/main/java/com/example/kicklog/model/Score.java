// 新規作成: com/example/kicklog/model/Score.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Score {
    @SerializedName("fullTime")
    private FullTimeScore fullTime;

    public FullTimeScore getFullTime() {
        return fullTime;
    }
}