// 新規作成: com.example/kicklog/model/Standing.java
package com.example.kicklog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Standing {
    @SerializedName("type")
    private String type; // "TOTAL", "HOME", "AWAY"

    @SerializedName("table")
    private List<TableEntry> table;

    public String getType() { return type; }
    public List<TableEntry> getTable() { return table; }
}