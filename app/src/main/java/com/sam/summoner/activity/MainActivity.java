package com.sam.summoner.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sam.summoner.GameStaticsManager;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private String ddVersion = "7.24.2";

    EditText summonerText;
    Button searchBtn;

    private RequestManager requestManager;
    private GameStaticsManager championManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestManager = new RequestManager(this);
        ddVersion = getDdragonVersion();

        championManager = new GameStaticsManager(this);

        // init search bar
        summonerText = (EditText) findViewById(R.id.summonerText);

        // init search button
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    // search for the summoner name in summonerText
    // if summonerText is empty, do nothing
    // if summonerText is not empty, search for summoner data
    private void search() {
        Log.d(TAG, "Starting summoner search...");
        String summonerName = summonerText.getText().toString();
        if (summonerName == "") {return;}
        String jString = requestManager.getAccountJObject(summonerName);
        if (jString != null) {
            Log.d(TAG, "Got summoner info. Launching InfoActivity...");
            Intent i = new Intent(this, InfoActivity.class);
            i.putExtra("jString", jString);
            i.putExtra("ddVersion", ddVersion);
            startActivity(i);
        } else {
            Log.e(TAG, "Failed to find summoner: " + summonerName);
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
    }

    // get and parse latest ddragon patch level, store in Constants
    private String getDdragonVersion() {
        Log.d(TAG, "Loading latest ddragon version code...");
        String ret = null;
        String jString = requestManager.getDdragonVersion();
        if (jString != null) {
            try {
                JSONArray jArray = new JSONArray(jString);
                ret = jArray.getString(0);
                Log.d(TAG, "Ddragon version loaded.");
            } catch (JSONException e) {
                Log.e(TAG, "JSON error when loading ddragon version: " + e);
            }
        }
        return ret;
    }

}
