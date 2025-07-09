// 新規作成: com/example/kicklog/model/FullTimeScore.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class FullTimeScore {
    @SerializedName("home")
    private Integer home; // スコアはnullの場合があるのでInteger型

    @SerializedName("away")
    private Integer away;

    public Integer getHome() { return home; }
    public Integer getAway() { return away; }
}