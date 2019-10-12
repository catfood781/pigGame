package com.example.piggame;
import android.util.Log;

import java.util.Random;

public class PigGame {
    private Random rand = new Random();
    public int p1Score = 0;
    public int p2Score = 0;
    public int pointTotal = 0;
    public boolean isP1Turn = true;    // else it is player 2's turn
    public boolean isGameOver = false; // game is running until isGameOver is true


    public int rollRandDie() {
        // Rolling die for value between 1 and 6
        int roll = rand.nextInt(6) + 1;
        Log.d("PigGame", "roll: " + roll);
        return roll;
    }

    public int getPlayer1Score() { return p1Score; }
    public int getPlayer2Score() { return p2Score; }


    public void resetGame() {
        p1Score = 0;
        p2Score = 0;
        pointTotal = 0;
        isP1Turn = true;
        isGameOver = false;
    }



}
