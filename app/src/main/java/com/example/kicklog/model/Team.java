package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Team {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("crest")
    private String crest; // ★確認または追加

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCrest() { // ★確認または追加
        return crest;
    }
    // Setters if needed
}