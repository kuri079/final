package com.example.kicklog;  // ← 必ず MainActivity と同じパッケージ名にする

public class Player {
    private int number;
    private String name;
    private String position;

    public Player(int number, String name, String position) {
        this.number = number;
        this.name = name;
        this.position = position;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "#" + number + " " + name;
    }
}
