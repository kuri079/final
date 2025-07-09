package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;

public class Player {
    // 🔽 --- ここから追加 --- 🔽
    @SerializedName("id")
    private int id;
    // 🔼 --- ここまで追加 --- 🔼

    @SerializedName("name")
    private String name;

    @SerializedName("nationality")
    private String nationality;

    // 🔽 --- ここから追加 --- 🔽
    public int getId() {
        return id;
    }
    // 🔼 --- ここまで追加 --- 🔼

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }
}