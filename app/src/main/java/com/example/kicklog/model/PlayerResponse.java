package com.example.kicklog.model;

import java.util.List;

/**
 * /v4/players など “選手＋統計” エンドポイント用モデル。
 * 必要フィールドだけ抜粋しているので、追加したい項目があれば
 * Player / Statistics クラスにフィールドを増やしてください。
 */
public class PlayerResponse {

    /** ルート配列 "players" or "response" などに合わせてください */
    public List<PlayerData> response;

    /* ──────────────── ネスト ──────────────── */

    /** 1 名分 */
    public static class PlayerData {
        public Player player;
        public List<Statistics> statistics;
    }

    /** プロフィール */
    public static class Player {
        public int id;
        public String name;
        public int age;
        public String nationality;
        public String photo;   // URL
    }

    /** 統計（シーズン × コンペティションごとに 1 要素） */
    public static class Statistics {
        public Games games;
        public Goals goals;
        public Passes passes;
        // 必要に応じてカード、ショットなど追加可
    }

    public static class Games {
        public int appearances;
        public String position;
        public boolean captain;
    }

    public static class Goals {
        public Integer total;   // null になることがあるので Integer
        public Integer assists;
    }

    public static class Passes {
        public Integer key;
        public Integer accuracy;
    }
}
