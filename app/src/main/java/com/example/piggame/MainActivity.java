package com.example.piggame;

//import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
//import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.KeyEvent;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Locale;


public class MainActivity extends Activity
implements OnClickListener, OnEditorActionListener {

    // Class variables
    private EditText player1EditText;
    private EditText player2EditText;
    private TextView turnTextView;
    private TextView player1ScoreLabel;
    private TextView player2ScoreLabel;
    private TextView pointScoreTextView;
    private TextView p1WinTextEdit; // Called it TextEdit instead of TextView :(
    private TextView p2WinTextEdit;
    private String p1TurnText = "Player 1's turn";
    private String p2TurnText = "Player 2's turn";
    private String turnText = "'s turn";
    private ImageView dieImageView;
    private Button rollButton;
    private Button endTurnButton;
    private Button newGameButton;
    private static int GOAL = 100;
    private String p1Name;
    private String p2Name;

    // The game
    PigGame game;


    // Define SharedPreferences object
    private SharedPreferences savedValues;
    // For logging and debugging
    private static final String TAG = "MainActivity";


    private void displayScores() {
        player1ScoreLabel.setText(String.format(Locale.US, "%d", game.getPlayer1Score()));
        player2ScoreLabel.setText(String.format(Locale.US, "%d", game.getPlayer2Score()));
    }

    public void displayDie(int dieValue) {
        int id = 0;
        switch (dieValue) {
            case 1:
                id = R.drawable.die1;
                break;
            case 2:
                id = R.drawable.die2;
                break;
            case 3:
                id = R.drawable.die3;
                break;
            case 4:
                id = R.drawable.die4;
                break;
            case 5:
                id = R.drawable.die5;
                break;
            case 6:
                id = R.drawable.die6;
                break;
        }
        dieImageView.setImageResource(id);
    }

    public void displayPointTotal(int total) {
        pointScoreTextView.setText(String.valueOf(total));
    }

    public void switchPlayerTurn() {
        // Switches player's turn, player turn text and re-enables roll button
        game.isP1Turn = !game.isP1Turn;
        turnTextView.setText(game.isP1Turn ? p1TurnText : p2TurnText);
        rollButton.setEnabled(true);
    }

    public void playerRoll() {
        // Random roll
        int dieRoll = game.rollRandDie();
        // Show die value
        displayDie(dieRoll);
        // If not 1, continue adding to accumulator
        if (dieRoll != 1) {
            game.pointTotal += dieRoll;
        } else {
        // Lose accumulated points for this turn and disable button
            game.pointTotal = 0;
            rollButton.setEnabled(false);
        }
        displayPointTotal(game.pointTotal);
    }

    public void endPlayerTurn() {
        // Check to see if there are points to add for p1 or p2
        if (game.pointTotal != 0 && game.isP1Turn) {
            game.p1Score += game.pointTotal;
            player1ScoreLabel.setText(String.valueOf(game.p1Score));
            game.p1CanPlay = false;
            game.p2CanPlay = true;
        } else if (game.pointTotal != 0 && !game.isP1Turn) {
            game.p2Score += game.pointTotal;
            player2ScoreLabel.setText((String.valueOf(game.p2Score)));
            game.p1CanPlay = true;
            game.p2CanPlay = false;
        }
        // Resets counter for other player.
        game.pointTotal = 0;
        checkForWinner();

    }

    public void checkForWinner() {
        boolean winnerFound = false;
        if (game.p1Score >= GOAL){
            game.p1CanPlay = false;
            winnerFound = true;
        } else if (game.p2Score >= GOAL) {
            game.p2CanPlay = false;
            winnerFound = true;
        }

        if (winnerFound && (!game.p1CanPlay || !game.p2CanPlay)) {
            if (game.p1Score > game.p2Score) {
                Log.d(TAG, "player 1 wins!");
                p1WinTextEdit.setVisibility(View.VISIBLE);
            } else if (game.p2Score > game.p1Score){
                Log.d(TAG, "player 2 wins!");
                p2WinTextEdit.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "a tie!");
            }
            disableGamePlayButtons();

        }

        switchPlayerTurn();
    }

    public void enableAllGamePlayButtons() {
        rollButton.setEnabled(true);
        endTurnButton.setEnabled(true);
    }

    public void disableGamePlayButtons() {
        rollButton.setEnabled(false);
        endTurnButton.setEnabled(false);
    }

    public void startNewGame() {
        game.resetGame();
        displayScores();
        displayPointTotal(0);
        turnTextView.setText(p1TurnText);
        enableAllGamePlayButtons();
        p1WinTextEdit.setVisibility(View.INVISIBLE);
        p2WinTextEdit.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player1EditText = (EditText)findViewById(R.id.player1EditText);
        player2EditText = (EditText)findViewById(R.id.player2EditText);
        turnTextView = (TextView)findViewById(R.id.turnTextView);
        player1ScoreLabel = (TextView)findViewById(R.id.player1ScoreLabel);
        player2ScoreLabel = (TextView)findViewById(R.id.player2ScoreLabel);
        pointScoreTextView = (TextView)findViewById(R.id.pointScoreTextView);
        p1WinTextEdit = (TextView)findViewById(R.id.p1WinTextEdit);
        p2WinTextEdit = (TextView)findViewById(R.id.p2WinTextEdit);
        dieImageView = (ImageView)findViewById(R.id.dieImageView);
        rollButton = (Button)findViewById(R.id.rollButton);
        endTurnButton = (Button)findViewById(R.id.endTurnButton);
        newGameButton = (Button)findViewById(R.id.newGameButton);


        game = new PigGame();
        turnTextView.setText(p1TurnText);
        rollButton.setFocusable(false);
        endTurnButton.setFocusable(false);
        newGameButton.setFocusable(false);
        p1WinTextEdit.setVisibility(View.INVISIBLE);
        p2WinTextEdit.setVisibility(View.INVISIBLE);

        displayScores();

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

    }


    @Override
    public boolean onEditorAction(TextView v, int actionID, KeyEvent event) {
        // Closes the soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return false;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rollButton:
                // Roll the die
                Log.d(TAG, "roll button pressed");

                if ((game.isP1Turn && game.p1CanPlay) ||(!game.isP1Turn && game.p2CanPlay)) {
                    playerRoll();
                }

                break;

            case R.id.endTurnButton:
                // end the turn and switch to the other player
                Log.d(TAG, "endTurn button pressed");
                endPlayerTurn();
                break;

            case R.id.newGameButton:
                // start a new game
                Log.d(TAG, "newGame button pressed");

                startNewGame();
                break;
        }
    }

//    @Override
//    public void onPause() {
//        Editor editor = savedValues.edit();
//        editor.putString("p1Name", player1EditText.getText().toString());
//        editor.putString("p2Name", player2EditText.getText().toString());
//        editor.putInt("p1Score", game.p1Score);
//        editor.putInt("p2Score", game.p2Score);
//        editor.apply();
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        player1EditText.setText(savedValues.getString("p1Name", ""));
//        player2EditText.setText(savedValues.getString("p2Name", ""));
//        player1ScoreLabel.setText(savedValues.getInt("p1Score", 0));
//        player2ScoreLabel.setText(savedValues.getInt("p2Score", 0));
//        displayScores();
//    }


}
