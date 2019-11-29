package com.example.piggame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
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



@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity
implements OnClickListener, OnEditorActionListener, OnSharedPreferenceChangeListener {

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
    private ImageView dieImageView;
    private Button rollButton;
    private Button endTurnButton;
    private Button newGameButton;
    private static int GOAL = 100;


    // The game
    private PigGame game;


    // Define SharedPreferences object
    private SharedPreferences prefs;
    private SharedPreferences savedValues;
    private boolean enableAI = false;
    private int pref_computer_roll;
    private int pref_computer_max_score;

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

    private boolean checkForPlayerName() {

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
        if (enableAI) {
            player2EditText.setText("Computer");
        }
        String turnText = "'s turn";
        p1TurnText = player1EditText.getText().toString() + turnText;
        p2TurnText = player2EditText.getText().toString() + turnText;
        turnTextView.setText(game.isP1Turn ? p1TurnText : p2TurnText);
        return true;
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

    public void endPlayerTurn() {
        // Check to see if there are points to add for p1 or p2
        if (game.getPointTotal() != 0 && game.isP1Turn) {
            game.setPlayer1Score(game.getPointTotal() + game.getPlayer1Score());
            player1ScoreLabel.setText(String.valueOf(game.getPlayer1Score()));
        } else if (game.getPointTotal() != 0 && !game.isP1Turn) {
            game.setPlayer2Score(game.getPointTotal() + game.getPlayer2Score());
            player2ScoreLabel.setText(String.valueOf(game.getPlayer2Score()));
        }
        // Resets counter for other player.
        game.setPointTotal(0);
        displayPointTotal(game.getPointTotal());
        checkForWinner();
        switchPlayerTurn();
        if (enableAI && !game.isP1Turn) {
            computerRoll();
        }
    }

    public void switchPlayerTurn() {
        // Switches player's turn, player turn text and re-enables roll button
        game.isP1Turn = !game.isP1Turn;
        turnTextView.setText(game.isP1Turn ? p1TurnText : p2TurnText);
        rollButton.setEnabled(true);
    }

    public void computerRoll() {
        for (int i = 0; i < game.numberOfRollsAllowed || game.getPointTotal() < game.numberOfScoreLimit; i++) {
            int dieRoll = game.rollRandDie();
            if (dieRoll != 1) {
                game.setPointTotal(game.getPointTotal() + dieRoll);
            } else {
                game.setPointTotal(0);
                break;
            }
            Log.d(TAG, "computerScore: " + game.getPlayer2Score());
            Log.d(TAG, "i: " + i);
        }
        displayPointTotal(game.getPointTotal());
        endPlayerTurn();
    }

    public void checkForWinner() {
        boolean winnerFound = false;
        if (game.getPlayer1Score() >= GOAL){
            if (!game.p1CanPlay) {
                winnerFound = true;
            }
            game.p1CanPlay = false;

        }
        if (game.getPlayer2Score() >= GOAL) {
            if (!game.p2CanPlay) {
                winnerFound = true;
            }
            game.p2CanPlay = false;
        }

        if (winnerFound) {
            if (game.getPlayer1Score() > game.getPlayer2Score()) {
                Log.d(TAG, "player 1 wins!");
                p1WinTextEdit.setVisibility(View.VISIBLE);
            } else if (game.getPlayer2Score() > game.getPlayer1Score()){
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
        player2EditText.setFocusable(true);

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

        game = new PigGame();
        displayScores();

        if (savedInstanceState != null) {
            game.setPlayer1Score(savedInstanceState.getInt("p1Score", 0));
            game.setPlayer2Score(savedInstanceState.getInt("p2Score", 0));
            player1ScoreLabel.setText(String.valueOf(game.getPlayer1Score()));
            player2ScoreLabel.setText(String.valueOf(game.getPlayer2Score()));

            player1EditText.setText(savedInstanceState.getString("p1Name", ""));
            player2EditText.setText(savedInstanceState.getString("p2Name", ""));
            turnTextView.setText(savedInstanceState.getString("turnTextView", "Player 1's turn"));
            game.isP1Turn = savedInstanceState.getBoolean("isP1Turn", true);


//            String pointScore = prefs.getString("pointScoreTextView", "0");
//
//            pointScoreTextView.setText(pointScore);
//            game.setPointTotal(Integer.parseInt(pointScore));

        }






        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        savedValues = getSharedPreferences("savedValues", MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("p1Score", game.getPlayer1Score());
        outState.putInt("p2Score", game.getPlayer2Score());

        outState.putString("p1Name", player1EditText.getText().toString());
        outState.putString("p2Name", player2EditText.getText().toString());
        outState.putString("turnTextView", turnTextView.getText().toString());
        outState.putBoolean("isP1Turn", game.isP1Turn);

        outState.putString("pointsView", pointScoreTextView.getText().toString());
        outState.putInt("pointTotal", game.getPointTotal());


        super.onSaveInstanceState(outState);
    }





    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            game.setPlayer1Score(savedInstanceState.getInt("p1Score", 0));
            game.setPlayer2Score(savedInstanceState.getInt("p2Score", 0));
            player1ScoreLabel.setText(String.valueOf(game.getPlayer1Score()));
            player2ScoreLabel.setText(String.valueOf(game.getPlayer2Score()));

            player1EditText.setText(savedInstanceState.getString("p1Name", ""));
            player2EditText.setText(savedInstanceState.getString("p2Name", ""));
            turnTextView.setText(savedInstanceState.getString("turnTextView", "Player 1's turn"));
            game.isP1Turn = savedInstanceState.getBoolean("isP1Turn", true);

            pointScoreTextView.setText(prefs.getString("pointsView", "0"));
            game.setPointTotal(savedInstanceState.getInt("pointTotal", 0));

        }

    }


    @Override
    public void onPause() {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putBoolean("pref_enable_ai", enableAI);
        editor.putInt("pref_computer_max_score", pref_computer_max_score);
        editor.putInt("pref_computer_roll", pref_computer_roll);
        editor.commit();

        super.onPause();
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
                if (checkForPlayerName()) { playerRoll(); }
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

    @Override
    public void onResume() {
        super.onResume();

        enableAI = prefs.getBoolean("pref_enable_ai", false);
        pref_computer_max_score = prefs.getInt("pref_computer_max_score", 15);
        pref_computer_roll = prefs.getInt("pref_computer_roll", 3);

        prefs.registerOnSharedPreferenceChangeListener(this);
    }




    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        if (key.equals("pref_enable_ai")) {
            enableAI = prefs.getBoolean(key, false);
        }
        if (key.equals("pref_computer_max_score")) {
            pref_computer_max_score = prefs.getInt(key, 15);
        }
        if (key.equals("pref_computer_roll")) {
            pref_computer_roll = prefs.getInt(key,3);
        }

    }



}
