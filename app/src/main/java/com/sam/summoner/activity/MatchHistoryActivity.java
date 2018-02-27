package com.sam.summoner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.gson.Gson;
import com.sam.summoner.Constants;
import com.sam.summoner.StaticsDatabaseHelper;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.match.MatchDto;
import com.sam.summoner.match.ParticipantDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MatchHistoryActivity extends AppCompatActivity {
    public static final String TAG = "MatchHistoryActivity";
    private Context mContext = this;

    private ArrayList<MatchDto> matches;
    private RequestManager requestManager;
    private StaticsDatabaseHelper helper;
    private ArrayList<String> matchStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        String jString = getIntent().getStringExtra("jString");

        requestManager = RequestManager.getInstance();
        helper = new StaticsDatabaseHelper(this);
        matchStrings = new ArrayList<String>();
        matches = new ArrayList<MatchDto>();

        new LoadUI().execute(jString);
    }

    private class LoadUI extends AsyncTask <String, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading match history...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            parseMatches(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            populateHistory();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    private void parseMatches(String jString) {
        Log.d(TAG, "Parsing matchlist...");
        try {
            JSONObject object = new JSONObject(jString);
            final JSONArray jArray = object.getJSONArray("matches");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject match = jArray.getJSONObject(i);
                long matchID = match.getLong("gameId");
                int champion = match.getInt("champion");
                String matchString = requestManager.getMatchData(matchID);
                MatchDto matchDto = new Gson().fromJson(matchString, MatchDto.class);
                matchDto.focusChamp = champion;
                matches.add(matchDto);
                matchStrings.add(matchString);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse matchlist: " + e);
        }
    }

    private void populateHistory() {
        Log.d(TAG, "Filling match history UI...");
        final LayoutInflater inflater = getLayoutInflater();
        final LinearLayout parent = (LinearLayout) findViewById(R.id.mhMatchList);
        for (int i = 0; i < matches.size(); i++){
            MatchDto matchDto = matches.get(i);
            populateMatch(inflater, parent, matchDto, i);
        }
    }

    private void populateMatch(LayoutInflater inflater, LinearLayout parent, MatchDto match, int i) {
        Log.d(TAG, "Formatting match...");
        final int ii = i;
        ParticipantDto player = match.getFocusPlayerInfo();
        View view = inflater.inflate(R.layout.layout_match_preview, parent, false);
        TextView textWin = (TextView) view.findViewById(R.id.playerName);
        if (player.stats.win) {
            textWin.setText("Win");
            view.setBackground(getDrawable(R.drawable.background5));
        } else {
            textWin.setText("Loss");
            view.setBackground(getDrawable(R.drawable.background4));
        }
        TextView textMode = (TextView) view.findViewById(R.id.matchMode);
        setMode(textMode, match.queueId);
        TextView textLevel = (TextView) view.findViewById(R.id.playerLevel);
        textLevel.setText(String.valueOf(player.stats.champLevel));
        TextView textDate = (TextView) view.findViewById(R.id.matchDate);
        Date date = new Date(match.gameCreation);
        textDate.setText(date.toString());
        TextView textStats = (TextView) view.findViewById(R.id.matchStats);
        setStats(textStats, player.stats.goldEarned, player.stats.kills, player.stats.deaths, player.stats.assists, match.gameDuration, player.stats.totalMinionsKilled);
        ImageView portrait = (ImageView) view.findViewById(R.id.playerChampPortrait);
        setPortrait(portrait, player.championId);
        setItemImages(view, player.getItems());
        setSummSpellImages(view, player.spell1Id, player.spell2Id);
        Log.d(TAG, "Match formatted. Adding...");
        parent.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMatchView(ii);
            }
        });

    }

    private void goToMatchView(int ind) {
        Log.d(TAG, "Going to full match view of match " + ind + " in list...");
        Intent i = new Intent(this, MatchActivity.class);
        i.putExtra("jString", matchStrings.get(ind));
        i.putExtra("matchID", matches.get(ind).gameId);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void setStats(TextView textStats, int gold, int kills, int deaths, int assists, long gameDuration, int cs) {
        Long l = new Long(gameDuration);
        double time = l.doubleValue();
        int mins = (int) Math.floor(time/60);
        int secs = (int) gameDuration % 60;

        String ret = "Gold: " + gold + " | CS: " + cs + " | KDA: " + kills + "/"
                + deaths + "/" + assists + "\n Gametime: " + mins + ":" + secs;
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
        if (items.get(0) != 0) {
            setItemimage(img1, items.get(0));
        }
        ImageView img2 = (ImageView) relativeLayout.findViewById(R.id.matchItem2);
        if (items.get(1) != 0) {
            setItemimage(img2, items.get(1));
        }
        ImageView img3 = (ImageView) relativeLayout.findViewById(R.id.matchItem3);
        if (items.get(2) != 0) {
            setItemimage(img3, items.get(2));
        }
        ImageView img4 = (ImageView) relativeLayout.findViewById(R.id.matchItem4);
        if (items.get(3) != 0) {
            setItemimage(img4, items.get(3));
        }
        ImageView img5 = (ImageView) relativeLayout.findViewById(R.id.matchItem5);
        if (items.get(4) != 0) {
            setItemimage(img5, items.get(4));
        }
        ImageView img6 = (ImageView) relativeLayout.findViewById(R.id.matchItem6);
        if (items.get(5) != 0) {
            setItemimage(img6, items.get(5));
        }
        ImageView img7 = (ImageView) relativeLayout.findViewById(R.id.matchItem7);
        if (items.get(6) != 0) {
            setItemimage(img7, items.get(6));
        }
    }

    private void setItemimage(ImageView img, int i) {
        if (i == 0) {
            img.setImageResource(R.drawable.empty_item);
            return;
        }
        String imgName = helper.getItemImgFromId(i);
        String url = requestManager.getItemImageURL(imgName);
        if (url == Constants.UNKNOWN_IMAGE) {}
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
                textMode.setText("Ranked Flex");
                break;
            case Constants.RANKED_3S_ID:
                textMode.setText("Ranked 3s");
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
