package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TeamDetailResponse {

    // ▼▼▼ crestなどのチーム情報を追加 ▼▼▼
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("crest")
    private String crest;
    // ▲▲▲ ここまで ▲▲▲

    @SerializedName("squad")
    private List<Player> squad;


    // ▼▼▼ 対応するゲッターを追加 ▼▼▼
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCrest() {
        return crest;
    }
    // ▲▲▲ ここまで ▲▲▲

    public List<Player> getSquad() {
        return squad;
    }
}