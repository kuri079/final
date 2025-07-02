// model/PlayerDetailResponse.java
package com.example.kicklog.model;

import java.util.List;

public class PlayerDetailResponse {

    public Player player;
    public List<Statistics> statistics;  // API は配列だが 1 シーズン指定なら 1 要素

    public static class Player {
        public int id;
        public String name;
        public int age;
        public String nationality;
        public String position;
        public String photo;
    }

    public static class Statistics {
        public Games games;
        public Goals goals;
        public Cards cards;
    }

    public static class Games {
        public int appearances;
        public int minutes;
    }
    public static class Goals {
        public Integer total;
        public Integer assists;
    }
    public static class Cards {
        public int yellow;
        public int red;
    }
}

