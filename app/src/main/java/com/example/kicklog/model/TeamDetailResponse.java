// app/src/main/java/com/example/kicklog/model/TeamDetailResponse.java
package com.example.kicklog.model;

import java.util.List;

public class TeamDetailResponse {

    public int    id;
    public String name;
    public String shortName;
    public String tla;        // 略称
    public String crest;      // ロゴ URL
    public Area   area;       // 国・地域
    public Coach  coach;      // 監督
    public List<Squad> squad; // スカッド一覧

    public static class Area  { public String name; }
    public static class Coach { public String name; }

    public static class Squad {
        public int    id;
        public String name;
        public String position;      // GK / MF / …
        public String nationality;
        // 他に dateOfBirth など必要なら追加
    }
}
