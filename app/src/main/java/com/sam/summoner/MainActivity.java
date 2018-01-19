package com.sam.summoner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    EditText summonerText;
    Button searchBtn;

    private RequestManager requestManager;
    private ChampionManager championManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestManager = new RequestManager();
        championManager = new ChampionManager(getApplicationContext());

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
        String url = requestManager.getAccountJOBject(summonerName);
        String jString = null;
        try {
            jString = new WebGrabber(this).execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show();
        }
        if (jString != null) {
            Log.d(TAG, "Summoner found. Moving to InfoActivity...");
            Intent i = new Intent(this, InfoActivity.class);
            i.putExtra("jString", jString);
            startActivity(i);
        } else {
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
    }
}
