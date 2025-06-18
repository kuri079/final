public class PlayerResponse {
    public List<PlayerData> response;

    public static class PlayerData {
        public Player player;
        public List<Statistics> statistics;
    }

    public static class Player {
        public String name;
        public int age;
        public String nationality;
        public String photo;
    }

    public static class Statistics {
        public Games games;
        public Goals goals;
    }

    public static class Games {
        public int appearances;
        public String position;
    }

    public static class Goals {
        public int total;
    }
}

