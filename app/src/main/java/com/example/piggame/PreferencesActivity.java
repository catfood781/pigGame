package com.example.piggame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.pref_enable_ai:
//                Toast.makeText(this, "enableAI", Toast.LENGTH_SHORT).show();
////                startActivity(new Intent(
////                        getApplicationContext(), PreferencesActivity.class
////                ));
//                return true;
//
//            case R.id.pref_computer_roll:
//                Toast.makeText(this, "computerRoll", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.pref_computer_max_score:
//                Toast.makeText(this, "maxScore", Toast.LENGTH_SHORT).show();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }









}
