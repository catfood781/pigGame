package com.example.piggame;
import android.util.Log;

import java.util.Random;

public class PigGame {
    private Random rand = new Random();
    private int p1Score = 0;
    private int p2Score = 0;
    private int pointTotal = 0;
    boolean isP1Turn = true;    // else it is player 2's turn

    boolean p1CanPlay = true;
    boolean p2CanPlay = true;

    // TODO: convert to private
    int numberOfRollsAllowed = 100;

    int numberOfScoreLimit = 100;

    private String player1Name = "";
    private String player2Name = "";

    public String getPlayer1Name() { return player1Name; }
    public void setPlayer1Name(String n) { player1Name = n; }
    public String getPlayer2Name() { return player2Name; }

    int getPlayer1Score() { return p1Score; }
    void setPlayer1Score(int value) { this.p1Score = value; }
    int getPlayer2Score() { return p2Score; }
    void setPlayer2Score(int value) { this.p2Score = value;}

    int getPointTotal() {
        return pointTotal;
    }
    void setPointTotal(int pointTotal) {
        this.pointTotal = pointTotal;
    }



    int rollRandDie() {
        // Rolling die for value between 1 and 6
        int roll = rand.nextInt(6) + 1;
        Log.d("PigGame", "roll: " + roll);
        return roll;
    }


    void resetGame() {
        p1Score = 0;
        p2Score = 0;
        setPointTotal(0);
        isP1Turn = true;

        p1CanPlay = true;
        p2CanPlay = true;

    }

}
