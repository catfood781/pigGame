package com.example.piggame;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


    }
}
