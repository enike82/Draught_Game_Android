package com.quincyapps.assignment002;

public class Circle {
    private int id;
    private int ownerId;
    private String startPosition;
    private String currentPosition;
    private boolean isKing;

    public Circle(int id, int ownerId, String startPosition) {
        this.id = id;
        this.ownerId = ownerId;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        this.isKing = false;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public boolean getIsKing() {
        return isKing;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setIsKing(boolean isKing) {
        this.isKing = isKing;
    }
}