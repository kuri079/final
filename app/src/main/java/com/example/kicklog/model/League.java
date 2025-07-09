// 新規作成: com/example/kicklog/model/League.java
package com.example.kicklog.model;

public class League {
    private String name;
    private String code; // APIで使うリーグコード (例: "PL", "BL1")

    public League(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}