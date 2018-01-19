package com.sam.summoner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class InfoActivity extends AppCompatActivity {
    private Summoner summoner;
    private RequestManager requestManager;
    private ChampionManager championManager;

    private TextView searchTxt;
    private Button searchBtn;
    private TextView nameView;
    private TextView levelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestManager = new RequestManager();


        summoner = new Summoner(null, null, null, null);
        String jString = getIntent().getStringExtra("jString");
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

        RelativeLayout soloQueue = (RelativeLayout) findViewById(R.id.rankedSoloLayout);
        soloQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        updateNameView();
        setRankedView();
    }

    //Get ranked information from a summoner, then update the layout's ranked information
    private void setRankedView() {
        String webString = requestManager.getRankJArray(summoner.getAccount().getSummonerID());
        String rankedString = "";
        try {
            rankedString = new WebGrabber(this).execute(webString).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show();
        }
        parseRankArray(rankedString);
        updateRankedView();
    }

    //Refresh name and level in layout for current summoner
    private void updateNameView() {
        String name = summoner.getAccount().getSummonerName();
        int lvl = summoner.getAccount().getSummonerLevel();
        setNameView(name, lvl);
    }

    //Helper
    //Set textviews
    private void setNameView(String name, int lvl) {
        nameView.setText(name);
        levelView.setText("Level " + lvl);
    }

    //Return an Account with summoner data in jString
    private Account parseAccount(String jString) {
        Account ret = null;
        int icon = -1;
        String name = null;
        int level = -1;
        int aid = -1;
        int sid = -1;

        try {
            JSONObject jAccount = new JSONObject(jString);
            icon = jAccount.getInt("profileIconId");
            name = jAccount.getString("name");
            level = jAccount.getInt("summonerLevel");
            aid = jAccount.getInt("accountId");
            sid = jAccount.getInt("id");
            ret = new Account(icon, name, level, sid, aid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void parseRankArray(String jString) {
        try {
            JSONArray jArray = new JSONArray(jString);
            for (int i = 0; i < 3; i++) {
                JSONObject jRank = null;
                try {jRank = jArray.getJSONObject(i);} catch (JSONException e) {}
                RankedInfo info = parseRankQueue(jRank);
                String queue = info.getQueue();
                switch (queue) {
                    case "RANKED_SOLO_5x5":
                        summoner.setSolo(info);
                        break;
                    case "RANKED_FLEX_SR":
                        summoner.setFlex(info);
                        break;
                    case "RANKED_FLEX_TT":
                        summoner.setTree(info);
                        break;
                }
            }
            if (summoner.getSolo() == null) {
                summoner.setSolo(new RankedInfo("RANKED_SOLO_5x5", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
            if (summoner.getFlex() == null) {
                summoner.setFlex(new RankedInfo("RANKED_FLEX_SR", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
            if (summoner.getTree() == null) {
                summoner.setTree(new RankedInfo("RANKED_FLEX_TT", "No Ranked Info", "UNRANKED", "", 0, 0, 0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RankedInfo parseRankQueue(JSONObject jRank) {
        RankedInfo rInfo = new RankedInfo("", "No Ranked Info", "UNRANKED", "", 0, 0, 0);
        if (jRank != null) {
            try {
                rInfo.setQueue(jRank.getString("queueType"));
                rInfo.setLeagueName(jRank.getString("leagueName"));
                rInfo.setLeaguePoints(jRank.getInt("leaguePoints"));
                rInfo.setLeagueRank(jRank.getString("rank"));
                rInfo.setLeagueTier(jRank.getString("tier"));
                rInfo.setWins(jRank.getInt("wins"));
                rInfo.setLosses(jRank.getInt("losses"));
            } catch (JSONException e) {
                e.printStackTrace();
                return rInfo;
            }
        }
        return rInfo;
    }

    private void updateRankedView() {
        // Ranked solo
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

    private void setImage(String tier, ImageView img) {
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

    private void search() {
        summoner = new Summoner(null, null, null, null);
        String summonerName = searchTxt.getText().toString();
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
            summoner.setAccount(parseAccount(jString));
            updateNameView();
            setRankedView();
        } else {
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void toMatchHistory() {

    }
}
