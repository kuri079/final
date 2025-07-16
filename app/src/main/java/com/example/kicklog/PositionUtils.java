package com.example.kicklog;

// 場所: com/example/kicklog/PositionUtils.java
public class PositionUtils {
    public static String abbreviate(String position) {
        if (position == null) {
            return "N/A";
        }

        switch (position) {
            case "Goalkeeper": return "GK";
            // DF
            case "Left-Back": return "LB";
            case "Right-Back": return "RB";
            case "Centre-Back": return "CB";
            case "Defence": return "CB";// ◀️ "Defence"を追加
            case "Defender": return "CB";
            // MF
            case "Defensive Midfield": return "DMF";
            case "Central Midfield": return "CMF";
            case "Attacking Midfield": return "AMF";
            case "Left Midfield": return "LMF";
            case "Right Midfield": return "RMF"; // ◀️ "Right Midfield"を追加
            case "Midfielder": return "MF";
            // FW
            case "Left Winger": return "LWG";
            case "Right Winger": return "RWG";
            case "Centre-Forward": return "CF";
            case "Second Striker": return "ST";
            case "Attacker": return "FW";
            default:
                return position;
        }
    }
}