package com.sam.summoner.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.summoner.Constants;
import com.sam.summoner.StaticsDatabaseHelper;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.account.Account;
import com.sam.summoner.account.RankedInfo;
import com.sam.summoner.account.Summoner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoActivity extends AppCompatActivity {
    private final String TAG = "InfoActivity";

    private Summoner summoner;
    private RequestManager requestManager;
    private StaticsDatabaseHelper helper;

    private TextView searchTxt;
    private Button searchBtn;
    private TextView nameView;
    private TextView levelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String jString = getIntent().getStringExtra("jString");

        requestManager = RequestManager.getInstance();
        helper = new StaticsDatabaseHelper(this);

        summoner = new Summoner(null, null, null, null);
        summoner.setAccount(parseAccount(jString));

        nameView = (TextView) findViewById(R.id.nameView);
        levelView = (TextView) findViewById(R.id.levelView);
        searchTxt = (TextView) findViewById(R.id.searchTxt);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        updateNameView();
        updateRankedView();

        Button soloMHbtn = (Button) findViewById(R.id.rankedSoloMHbtn);
        Button flexMHbtn = (Button) findViewById(R.id.rankedFlexMHbtn);
        Button treeMHbtn = (Button) findViewById(R.id.ranked3sMHbtn);
        soloMHbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMatchHistory(Constants.RANKED_SOLO_ID);
            }
        });
        flexMHbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMatchHistory(Constants.RANKED_FLEX_ID);
            }
        });
        treeMHbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMatchHistory(Constants.RANKED_3S_ID);
            }
        });

    }

    // get ranked information from a summoner, then update the layout's ranked information
    private void updateRankedView() {
        Log.d(TAG, "Updating ranked views...");
        String rankedString = requestManager.getRankJArray(summoner.getAccount().getSummonerID());
        parseRankArray(rankedString);
        updateRankedDesc();
    }

    // refresh name and level in layout for current summoner
    private void updateNameView() {
        Log.d(TAG, "Updating name views...");
        String name = summoner.getAccount().getSummonerName();
        long lvl = summoner.getAccount().getSummonerLevel();
        nameView.setText(name);
        levelView.setText("Level " + lvl);
    }

    // return an Account with summoner data in jString
    private Account parseAccount(String jString) {
        Log.d(TAG, "Parsing account information from summoner jString...");
        try {
            JSONObject jAccount = new JSONObject(jString);
            int icon = jAccount.getInt("profileIconId");
            String name = jAccount.getString("name");
            int level = jAccount.getInt("summonerLevel");
            int aid = jAccount.getInt("accountId");
            int sid = jAccount.getInt("id");
            return new Account(icon, name, level, sid, aid);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse account information: " + e);
        }
        return null;
    }

    // from a jArray containing information on a summoner's ranked queues, set summoner ranked data accordingly
    private void parseRankArray(String jString) {
        Log.d(TAG, "Parsing ranked queue information from jString...");
        try {
            JSONArray jArray = new JSONArray(jString);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jRank = null;
                try {jRank = jArray.getJSONObject(i);} catch (JSONException e) {}
                RankedInfo info = parseRankQueue(jRank);
                String queue = info.getQueue();
                switch (queue) {
                    case "RANKED_SOLO_5x5":
                        Log.d(TAG, "Solo queue parsed.");
                        summoner.setSolo(info);
                        break;
                    case "RANKED_FLEX_SR":
                        Log.d(TAG, "Flex queue parsed.");
                        summoner.setFlex(info);
                        break;
                    case "RANKED_FLEX_TT":
                        Log.d(TAG, "3s queue parsed.");
                        summoner.setTree(info);
                        break;
                }
            }
            if (summoner.getSolo() == null) {
                Log.d(TAG, "No solo queue information found.");
                summoner.setSolo(new RankedInfo("RANKED_SOLO_5x5", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
            if (summoner.getFlex() == null) {
                Log.d(TAG, "No flex queue information found.");
                summoner.setFlex(new RankedInfo("RANKED_FLEX_SR", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
            if (summoner.getTree() == null) {
                Log.d(TAG, "No 3s queue information found.");
                summoner.setTree(new RankedInfo("RANKED_FLEX_TT", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse ranked queue information: " + e);
        }
    }
    // helper
    // parse RankedInfo objects from jArray
    private RankedInfo parseRankQueue(JSONObject jRank) {
        Log.d(TAG, "Parsing RankedInfo object from jArray...");
        try {
            return new RankedInfo(
            jRank.getString("queueType"),
            jRank.getString("leagueName"),
            jRank.getString("tier"),
            jRank.getString("rank"),
            jRank.getInt("leaguePoints"),
            jRank.getInt("wins"),
            jRank.getInt("losses"));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse RankedInfo object: " + e);
        }
        return null;
    }

    // set text for summoner ranked queue information
    private void updateRankedDesc() {
        // Ranked solo
        Log.d(TAG, "Setting solo queue text...");
        ImageView img1 = (ImageView) findViewById(R.id.rankedSoloImg);
        setImage(summoner.getSolo().getLeagueTier(), img1);
        TextView rankedSoloTitle = (TextView) findViewById(R.id.rankedSoloTitle);
        rankedSoloTitle.setText(summoner.getSolo().getLeagueName() + " - " + summoner.getSolo().getLeagueTier() + " " + summoner.getSolo().getLeagueRank());
        String rankedSoloBody = "Wins: " + summoner.getSolo().getWins() + " | Losses: " + summoner.getSolo().getLosses() + " | " + summoner.getSolo().getLeaguePoints() + " LP";
        TextView rankedSoloText = (TextView) findViewById(R.id.rankedSoloWinLoss);
        rankedSoloText.setText(rankedSoloBody);
        ProgressBar prog1 = (ProgressBar) findViewById(R.id.rankedSoloWinrate);
        prog1.setMax(summoner.getSolo().getWins() + summoner.getSolo().getLosses());
        prog1.setProgress(summoner.getSolo().getWins());
        prog1.getProgressDrawable().setColorFilter(
                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);

        // Ranked flex
        Log.d(TAG, "Setting flex queue text...");
        ImageView img2 = (ImageView) findViewById(R.id.rankedFlexImg);
        setImage(summoner.getFlex().getLeagueTier(), img2);
        TextView rankedFlexTitle = (TextView) findViewById(R.id.rankedFlexTitle);
        rankedFlexTitle.setText(summoner.getFlex().getLeagueName() + " - " + summoner.getFlex().getLeagueTier() + " " + summoner.getFlex().getLeagueRank());
        String rankedFlexBody = "Wins: " + summoner.getFlex().getWins() + " | Losses: " + summoner.getFlex().getLosses() + " | " + summoner.getFlex().getLeaguePoints() + " LP";
        TextView rankedFlexText = (TextView) findViewById(R.id.rankedFlexWinLoss);
        rankedFlexText.setText(rankedFlexBody);
        ProgressBar prog2 = (ProgressBar) findViewById(R.id.rankedFlexWinrate);
        prog2.setMax(summoner.getFlex().getWins() + summoner.getFlex().getLosses());
        prog2.setProgress(summoner.getFlex().getWins());
        prog2.getProgressDrawable().setColorFilter(
                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);

        // Ranked 3s
        Log.d(TAG, "Setting 3s queue text...");
        ImageView img3 = (ImageView) findViewById(R.id.ranked3sImg);
        setImage(summoner.getTree().getLeagueTier(), img3);
        TextView ranked3sTitle = (TextView) findViewById(R.id.ranked3sTitle);
        ranked3sTitle.setText(summoner.getTree().getLeagueName() + " - " + summoner.getTree().getLeagueTier() + " " + summoner.getTree().getLeagueRank());
        String ranked3sBody = "Wins: " + summoner.getTree().getWins() + " | Losses: " + summoner.getTree().getLosses() + " | " + summoner.getTree().getLeaguePoints() + " LP";
        TextView ranked3sText = (TextView) findViewById(R.id.ranked3sWinLoss);
        ranked3sText.setText(ranked3sBody);
        ProgressBar prog3 = (ProgressBar) findViewById(R.id.ranked3sWinrate);
        prog3.setMax(summoner.getTree().getWins() + summoner.getTree().getLosses());
        prog3.setProgress(summoner.getTree().getWins());
        prog3.getProgressDrawable().setColorFilter(
                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    // set emblems for ranked queues
    private void setImage(String tier, ImageView img) {
        Log.d(TAG, "Setting queue image: " + tier);
        switch (tier) {
            case "UNRANKED":
                img.setImageResource(R.drawable.provisional);
                break;
            case "BRONZE":
                img.setImageResource(R.drawable.bronze);
                break;
            case "SILVER":
                img.setImageResource(R.drawable.silver);
                break;
            case "GOLD":
                img.setImageResource(R.drawable.gold);
                break;
            case "PLATINUM":
                img.setImageResource(R.drawable.platinum);
                break;
            case "DIAMOND":
                img.setImageResource(R.drawable.diamond);
                break;
            case "MASTER":
                img.setImageResource(R.drawable.master);
                break;
            case "CHALLENGER":
                img.setImageResource(R.drawable.challenger);
                break;
        }
    }

    // refresh the page for a new summoner search
    private void search() {
        Log.d(TAG, "Searching for a new summonger...");
        summoner = new Summoner(null, null, null, null);
        String summonerName = searchTxt.getText().toString();
        if (summonerName == "") {
            Log.d(TAG, "Search bar is empty.");
            return;
        }
        String jString = requestManager.getAccountJObject(summonerName);
        if (jString != null) {
            summoner.setAccount(parseAccount(jString));
            updateNameView();
            updateRankedView();
        } else {
            Log.e(TAG, "Failed to find summoner: jString is null.");
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void viewMatchHistory(int queue) {
        Log.d(TAG, "Starting match history load for queue: " + queue);
        String jString = requestManager.getMatchHistoryJObject(summoner.getAccount().getAccountID(), queue, Constants.MATCH_HISTORY_LENGTH);
        if (jString != null) {
            Intent i = new Intent(this, MatchHistoryActivity.class);
            i.putExtra("jString", jString);
            startActivity(i);
        } else {
            Log.e(TAG, "Failed to load match history: jString is null.");
        }
    }


}
