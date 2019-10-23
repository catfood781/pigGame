package com.example.piggame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.prefs.Preferences;


public class MainActivity extends AppCompatActivity
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


    // The game
    private PigGame game;


    // Define SharedPreferences object
    private SharedPreferences prefs;
    private boolean pref_enable_ai = false;
    private int pref_computer_roll = 3;
    private int pref_computer_max_score = 15;

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
            game.setPointTotal(game.getPointTotal() + dieRoll);
        } else {
        // Lose accumulated points for this turn and disable button
            game.setPointTotal(0);
            rollButton.setEnabled(false);
        }
        displayPointTotal(game.getPointTotal());
    }

    public void computerRoll() {
        for (int i = 0; i < game.numberOfRollsAllowed && game.getPointTotal() < game.numberOfScoreLimit; i++) {
            playerRoll();
        }
        endPlayerTurn();
    }

    private boolean checkForPlayerName() {
        if (pref_enable_ai) {
            player2EditText.setText("Computer");
        }
        if (player1EditText.getText().toString().equals("") || player2EditText.getText().toString().equals("")) {
            if (player1EditText.getText().toString().equals("")) {
                player1EditText.setFocusable(true);
                Toast.makeText(this, "Please enter a name.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                player1EditText.setFocusable(false);
            }
            if (player2EditText.getText().toString().equals("")) {
                player2EditText.setFocusable(true);
                Toast.makeText(this, "Please enter a name.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                player2EditText.setFocusable(false);
            }
        }
        p1TurnText = player1EditText.getText().toString() + turnText;
        p2TurnText = player2EditText.getText().toString() + turnText;
        turnTextView.setText(game.isP1Turn ? p1TurnText : p2TurnText);
        return true;
    }

    public void endPlayerTurn() {
        // Check to see if there are points to add for p1 or p2
        if (game.getPointTotal() != 0 && game.isP1Turn) {
            game.p1Score += game.getPointTotal();
            player1ScoreLabel.setText(String.valueOf(game.p1Score));
        } else if (game.getPointTotal() != 0 && !game.isP1Turn) {
            game.p2Score += game.getPointTotal();
            player2ScoreLabel.setText((String.valueOf(game.p2Score)));
        }
        // Resets counter for other player.
        game.setPointTotal(0);
        displayPointTotal(game.pointTotal);
        checkForWinner();
        switchPlayerTurn();
        if (pref_enable_ai) {
            computerRoll();
        }
    }

    public void checkForWinner() {
        boolean winnerFound = false;

        if (game.p1Score >= GOAL){
            if (!game.p1CanPlay) {
                winnerFound = true;
            }
            game.p1CanPlay = false;

        }
        if (game.p2Score >= GOAL) {
            if (!game.p2CanPlay) {
                winnerFound = true;
            }
            game.p2CanPlay = false;

        }

        if (winnerFound) {
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
        player1EditText.setText("");
        player2EditText.setText("");
        turnTextView.setText("Player 1's turn");
        player1EditText.setFocusable(true);
    }



    //------------------ Callback methods ----------------------//



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(
                        getApplicationContext(), PreferencesActivity.class
                ));
                return true;

            case R.id.menu_about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Deprecated, must investigate
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        rollButton.setOnClickListener(this);
        endTurnButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);

        turnTextView.setText(p1TurnText);
        rollButton.setFocusable(false);
        endTurnButton.setFocusable(false);
        newGameButton.setFocusable(false);
        p1WinTextEdit.setVisibility(View.INVISIBLE);
        p2WinTextEdit.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            game.p1Score = savedInstanceState.getInt("p1Score", 0);
            game.p2Score = savedInstanceState.getInt("p2Score", 0);
            player1EditText.setText(savedInstanceState.getString("p1Name", ""));
            player2EditText.setText(savedInstanceState.getString("p2Name", ""));
            game.pointTotal = savedInstanceState.getInt("pointTotal", 0);
            game.isP1Turn = savedInstanceState.getBoolean("isP1Turn", true);
            turnTextView.setText(savedInstanceState.getString("turnTextView", "Player 1's turn"));
            pref_computer_max_score = prefs.getInt("pref_computer_max_score", 15);
            pref_computer_roll = prefs.getInt("pref_computer_roll", 3);
            pref_enable_ai = prefs.getBoolean("pref_enable_ai", false);
            pointScoreTextView.setText(prefs.getString("pointsView", "0"));
        }

        game = new PigGame(10, 50, false);

        displayScores();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("p1Score", game.p1Score);
        outState.putInt("p2Score", game.p2Score);
        outState.putString("p1Name", player1EditText.getText().toString());
        outState.putString("p2Name", player2EditText.getText().toString());
        outState.putInt("pointTotal", game.pointTotal);
        outState.putBoolean("isP1Turn", game.isP1Turn);
        outState.putString("turnTextView", turnTextView.getText().toString());
        outState.putInt("pref_computer_max_score", pref_computer_max_score);
        outState.putInt("pref_computer_roll", pref_computer_roll);
        outState.putBoolean("pref_enable_ai", pref_enable_ai);
        outState.putString("pointsView", pointScoreTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            game.p1Score = savedInstanceState.getInt("p1Score", 0);
            game.p2Score = savedInstanceState.getInt("p2Score", 0);
            player1EditText.setText(savedInstanceState.getString("p1Name", ""));
            player2EditText.setText(savedInstanceState.getString("p2Name", ""));
            game.pointTotal = savedInstanceState.getInt("pointTotal", 0);
            game.isP1Turn = savedInstanceState.getBoolean("isP1Turn", true);
            turnTextView.setText(savedInstanceState.getString("turnTextView", "Player 1's turn"));
            pref_computer_max_score = prefs.getInt("pref_computer_max_score", 15);
            pref_computer_roll = prefs.getInt("pref_computer_roll", 3);
            pref_enable_ai = prefs.getBoolean("pref_enable_ai", false);
            pointScoreTextView.setText(prefs.getString("pointsView", "0"));
        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        pref_computer_max_score = prefs.getInt("pref_computer_max_score", 15);
//        pref_computer_roll = prefs.getInt("pref_computer_roll", 3);
//        pref_enable_ai = prefs.getBoolean("pref_enable_ai", false);
//
//    }

//    @Override
//    public void onPause() {
//        // save the instance variables
////        prefs.unregisterOnSharedPreferenceChangeListener(this);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("pref_computer_max_score", pref_computer_max_score);
//        editor.putInt("pref_computer_roll", pref_computer_roll);
//        editor.putBoolean("pref_enable_ai", pref_enable_ai);
//        editor.commit();
//
//        super.onPause();
//    }


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

                if (checkForPlayerName()) {

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


}
