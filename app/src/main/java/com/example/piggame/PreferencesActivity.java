package com.example.piggame;

import android.app.Activity;
import android.os.Bundle;

class PreferencesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display fragment as main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

    }
}
