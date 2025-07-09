// 新規作成: com/example/kicklog/model/TeamDetailResponse.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TeamDetailResponse {

    // APIのレスポンスに含まれる "squad" というキーの選手リストに対応します
    @SerializedName("squad")
    private List<Player> squad;

    public List<Player> getSquad() {
        return squad;
    }
}