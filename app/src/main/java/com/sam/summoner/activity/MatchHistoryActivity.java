package com.sam.summoner.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sam.summoner.Constants;
import com.sam.summoner.ImageRequestManager;
import com.sam.summoner.LocalDatabaseHelper;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.match.Match;
import com.sam.summoner.match.PlayerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class MatchHistoryActivity extends AppCompatActivity {
    public static final String TAG = "MatchHistoryActivity";

    private ArrayList<Match> matches;
    private String ddVersion;
    private RequestManager requestManager;
    private LocalDatabaseHelper helper;
    private ImageRequestManager imgRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        String jString = getIntent().getStringExtra("jString");
        ddVersion = getIntent().getStringExtra("ddVersion");

        requestManager = new RequestManager(this);
        requestManager.setDdVersion(ddVersion);
        imgRequestManager = ImageRequestManager.getInstance(this);

        helper = new LocalDatabaseHelper(this);

        matches = new ArrayList<Match>();
        parseMatches(jString);
        populateHistory();
    }

    private void parseMatches(String jString) {
        Log.d(TAG, "Parsing matchlist...");
        try {
            JSONObject object = new JSONObject(jString);
            JSONArray jArray = object.getJSONArray("matches");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject match = jArray.getJSONObject(i);
                long matchID = match.getLong("gameId");
                int championID = match.getInt("champion");
                Match newMatch = new Match(matchID, championID);
                parseMatch(newMatch);
                matches.add(newMatch);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse matchlist: " + e);
        }
    }

    private void parseMatch(Match match) {
        String jString = requestManager.getMatchData(match.getGameID());
        match.populateMatch(jString);
    }

    private void populateHistory() {
        Log.d(TAG, "Filling match history UI...");
        final LayoutInflater inflater = getLayoutInflater();
        final LinearLayout parent = (LinearLayout) findViewById(R.id.mhMatchList);
        for (final Match match : matches) {
            Thread t = new Thread(new Runnable() {
                LayoutInflater infl = inflater;
                LinearLayout par = parent;
                Match m = match;
                @Override
                public void run() {
                    populateMatch(infl, parent, m);
                }
            });
            t.setPriority(Thread.NORM_PRIORITY - 1);
            t.run();
        }
    }

    private void populateMatch(LayoutInflater inflater, LinearLayout parent, Match match) {
        Log.d(TAG, "Formatting match...");
        PlayerInfo info = match.getFocusPlayerInfo();
        View view = inflater.inflate(R.layout.layout_match_preview, parent, false);
        TextView textWin = (TextView) view.findViewById(R.id.matchWin);
        if (info.getWin()) {
            textWin.setText("Win");
        } else {
            textWin.setText("Loss");
        }
        TextView textMode = (TextView) view.findViewById(R.id.matchMode);
        setMode(textMode, match.getQueueID());
        TextView textLevel = (TextView) view.findViewById(R.id.matchLevel);
        textLevel.setText(String.valueOf(info.getChampLevel()));
        TextView textDate = (TextView) view.findViewById(R.id.matchDate);
        Date date = new Date(match.getGameDate());
        textDate.setText(date.toString());
        TextView textStats = (TextView) view.findViewById(R.id.matchStats);
        setStats(textStats, info.getGold(), info.getKills(), info.getDeaths(), info.getAssists(), match.getGameDuration(), info.getCs());
        ImageView portrait = (ImageView) view.findViewById(R.id.matchChampPortrait);
        setPortrait(portrait, info.getChampionID());
        setItemImages(view, info.getItems());
        setSummSpellImages(view, info.getSpellID1(), info.getSpellID2());
        Log.d(TAG, "Match formatted. Adding...");
        parent.addView(view);
    }

    private void setStats(TextView textStats, int gold, int kills, int deaths, int assists, long gameDuration, int cs) {
        Time time = new Time(gameDuration);
        String ret = "Gold: " + gold + " | CS: " + cs + " | KDA: " + kills + "/"
                + deaths + "/" + assists + " | Gametime: " + time.toString();
        textStats.setText(ret);
    }

    private void setSummSpellImages(View view, int spellID1, int spellID2) {
        ImageView ss1 = (ImageView) view.findViewById(R.id.matchSumm1);
        String spellName1 = helper.getSpellImgFromId(spellID1);
        String url1 = requestManager.getSpellImageURL(spellName1);
        setImg(ss1, url1);
        ImageView ss2 = (ImageView) view.findViewById(R.id.matchSumm2);
        String spellName2 = helper.getSpellImgFromId(spellID2);
        String url2 = requestManager.getSpellImageURL(spellName2);
        setImg(ss2, url2);
    }

    private void setItemImages(View view, ArrayList<Integer> items) {
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.matchItemLayout);
        ImageView img1 = (ImageView) relativeLayout.findViewById(R.id.matchItem1);
        setItemimage(img1, items.get(0));
        ImageView img2 = (ImageView) relativeLayout.findViewById(R.id.matchItem2);
        setItemimage(img2, items.get(1));
        ImageView img3 = (ImageView) relativeLayout.findViewById(R.id.matchItem3);
        setItemimage(img3, items.get(2));
        ImageView img4 = (ImageView) relativeLayout.findViewById(R.id.matchItem4);
        setItemimage(img4, items.get(3));
        ImageView img5 = (ImageView) relativeLayout.findViewById(R.id.matchItem5);
        setItemimage(img5, items.get(4));
        ImageView img6 = (ImageView) relativeLayout.findViewById(R.id.matchItem6);
        setItemimage(img6, items.get(5));
        ImageView img7 = (ImageView) relativeLayout.findViewById(R.id.matchItem7);
        setItemimage(img7, items.get(6));
    }

    private void setItemimage(ImageView img, int i) {
        if (i == 0) {return;}
        String imgName = helper.getItemImgFromId(i);
        String url = requestManager.getItemImageURL(imgName);
        setImg(img, url);
    }

    private void setPortrait(ImageView portrait, int championID) {
        String imgName = helper.getChampionImgFromId(championID);
        String url = requestManager.getChampionImageURL(imgName);
        setImg(portrait, url);
    }

    private void setMode(TextView textMode, int queueID) {
        switch (queueID) {
            case Constants.RANKED_SOLO_ID:
                textMode.setText("Ranked Solo");
                break;
            case Constants.RANKED_FLEX_ID:
                textMode.setText("Ranked Solo");
                break;
            case Constants.RANKED_3S_ID:
                textMode.setText("Ranked Solo");
                break;
            default:
                textMode.setText("Other");
                break;
        }
    }

    private void setImg(ImageView img, String url) {
        Glide.with(this).load(url).into(img);
    }

}
