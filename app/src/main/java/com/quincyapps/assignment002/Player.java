package com.quincyapps.assignment002;

import java.util.ArrayList;

public class Player {
    public static int playerCount = 0;
    public static int playerTileLength = 12;

    private int id;
    private String name;
    private ArrayList<Circle> playerButton = new ArrayList<Circle>();

    public Player(String[] startPostions) {
        if (Player.getPlayerCount() >= 2) {
            Player.setPlayerCount(0);
        }
        Player.playerCount += 1;
        this.id = Player.playerCount;
        this.name = String.format("Player %s", Player.playerCount);
        for (int i=0; i<Player.playerTileLength; i++) {
            this.playerButton.add(new Circle(
                    i+1, this.id, startPostions[i]
            ));
        }
    }

    public static int getPlayerCount() {
        return playerCount;
    }

    public static int getPlayerTileLength() {
        return playerTileLength;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Circle> getPlayerButton() {
        return playerButton;
    }

    public static void setPlayerCount(int playerCount) {
        Player.playerCount = playerCount;
    }
}
