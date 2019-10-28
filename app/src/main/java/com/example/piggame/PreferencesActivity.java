package com.example.piggame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity {

    private SharedPreferences prefs;
    private boolean enable_ai;
    private int computer_max_score;
    private int computer_roll;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get preferences
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }



}
