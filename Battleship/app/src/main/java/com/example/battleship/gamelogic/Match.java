package com.example.battleship.gamelogic;

public class Match {
    private String player1Id;
    private String player2Id;

    // Default constructor required for calls to DataSnapshot.getValue(Match.class)
    public Match() {}

    public Match(String player1Id) {
        this.player1Id = player1Id;
        this.player2Id = null;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public boolean hasSecondPlayer() {
        return player2Id != null;
    }
}
