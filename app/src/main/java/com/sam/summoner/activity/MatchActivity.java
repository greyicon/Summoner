package com.sam.summoner.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sam.summoner.LocalDatabaseHelper;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.match.Match;
import com.sam.summoner.match.PlayerInfo;

import java.util.ArrayList;

public class MatchActivity extends AppCompatActivity {
    public static final String TAG = "MatchActivity";

    private RequestManager requestManager;
    private LocalDatabaseHelper helper;
    private Match match;

    private int winningTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        requestManager = RequestManager.getInstance();
        helper = new LocalDatabaseHelper(this);

        String jString = getIntent().getStringExtra("jString");

        match = new Match(jString);

        winningTeam = match.getWinner();
        ArrayList<PlayerInfo> blueTeam = match.getTeam(100);
        ArrayList<PlayerInfo> redTeam = match.getTeam(200);
    }
}
