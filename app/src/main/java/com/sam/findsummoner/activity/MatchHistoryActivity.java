package com.sam.findsummoner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sam.findsummoner.CacheDatabaseHelper;
import com.sam.findsummoner.Constants;
import com.sam.findsummoner.R;
import com.sam.findsummoner.RequestManager;
import com.sam.findsummoner.StaticsDatabaseHelper;
import com.sam.findsummoner.match.MatchDto;
import com.sam.findsummoner.match.ParticipantDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MatchHistoryActivity extends AppCompatActivity {
    public static final String TAG = "MHActivity";
    private Context mContext = this;

    private RequestManager mRequestManager;
    private StaticsDatabaseHelper mHelper;
    private CacheDatabaseHelper mCache;
    private ArrayList<MatchDto> matches;
    private ArrayList<String> matchStrings;

    private Thread inflationThread;
    private BlockingQueue inflationQueue = new ArrayBlockingQueue(10);

    private long accountId;
    private int queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Creating " + TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        matches = new ArrayList<>();
        matchStrings = new ArrayList<>();

        String jString = getIntent().getStringExtra("jString");
        accountId = getIntent().getLongExtra("accountId", 0);
        queue = getIntent().getIntExtra("queue", 0);

        // Launch thread to inflate matches as soon as they are downloaded and parsed
        Log.d(TAG, "Starting inflation thread");
        startInflationThread();

        new LoadUI().execute(jString);
    }

    private class LoadUI extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;
        private final String TAG_SUFFIX = ".LoadUI";

        @Override
        protected void onPreExecute() {
            Log.d(TAG + TAG_SUFFIX, "onPreExecute()");
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG + TAG_SUFFIX, "doInBackground()");
            initBackEnd();
            getMatches(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG + TAG_SUFFIX, "onPostExecute()");
            try {
                Log.d(TAG, "Closing inflation thread");
                inflationThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Inflation thread couldn't join.");
                Toast.makeText(mContext, "Loading took too long.", Toast.LENGTH_SHORT).show();
            }
            initFrontEnd();
            if (dialog.isShowing()) {dialog.dismiss();}
        }
    }

    private void initBackEnd() {
        Log.d(TAG, "initBackEnd()");
        mRequestManager = RequestManager.getInstance();
        mHelper = new StaticsDatabaseHelper(mContext);
        mCache = new CacheDatabaseHelper(this);
    }

    private void initFrontEnd() {
        Log.d(TAG, "initFrontEnd()");
        TextView loadingLabel = findViewById(R.id.loadingLabel);
        loadingLabel.setVisibility(View.GONE);
        Button moreGames = findViewById(R.id.moreGamesBtn);
        moreGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreGames();
            }
        });
    }

    private void startInflationThread() {
        final int start = matches.size();
        final int end = start + Constants.MATCH_HISTORY_LENGTH;
        inflationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    final MatchDto match;
                    try {
                        final int index = i;
                        match = (MatchDto) inflationQueue.poll((long) 2, TimeUnit.SECONDS);
                        // Must run on main thread
                        //   Cannot touch view hierarchies between threads
                        //   Glide must run on main thread
                        runOnUiThread(new Runnable() {
                            final LayoutInflater inflater = getLayoutInflater();
                            final LinearLayout parent = (LinearLayout) findViewById(R.id.mhMatchList);
                            @Override
                            public void run() {
                                populateMatch(inflater, parent, match, index);
                            }
                        });
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Took too long to load match.");
                    }
                }
            }
        });
        inflationThread.setPriority(Thread.NORM_PRIORITY + 2);
        inflationThread.start();
    }

    private void loadMoreGames() {
        startInflationThread();
        new LoadMoreUI().execute();
    }

    private class LoadMoreUI extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        private final String TAG_SUFFIX = ".LoadMoreUI";

        @Override
        protected void onPreExecute() {
            Log.d(TAG + TAG_SUFFIX, "onPreExecute()");
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG + TAG_SUFFIX, "doInBackground()");
            int start = matches.size();
            int end = matches.size() + Constants.MATCH_HISTORY_LENGTH;
            String moreGamesJString = mRequestManager.getMatchHistoryJObject(accountId, queue,
                    start, end);
            getMatches(moreGamesJString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG + TAG_SUFFIX, "onPostExecute()");
            try {
                Log.d(TAG, "Closing inflation thread");
                inflationThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Inflation thread couldn't join.");
                Toast.makeText(mContext, "Loading took too long.", Toast.LENGTH_SHORT).show();
            }
            if (dialog.isShowing()) {dialog.dismiss();}
        }
    }

    private void getMatches(String jString) {
        Log.d(TAG, "getMatches()");
        try {
            JSONObject object = new JSONObject(jString);
            final JSONArray jArray = object.getJSONArray("matches");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject match = jArray.getJSONObject(i);
                long matchID = match.getLong("gameId");
                int champion = match.getInt("champion");

                String matchString;
                String cacheId = String.valueOf(matchID);
                Log.w(TAG, "Checking cache for match data: " + cacheId);
                if (mCache.containsId(cacheId)) {
                    Log.w(TAG, "Data found in cache. Importing...");
                    matchString = mCache.getData(cacheId);
                } else {
                    Log.w(TAG, "Data not found in cache. Downloading...");
                    matchString = mRequestManager.getMatchData(matchID);
                    boolean added = mCache.addData(String.valueOf(matchID), matchString);
                    if (!added) {
                        mCache.clearCache();
                        mCache.addData(cacheId, matchString);
                    }
                }
                Log.w(TAG, "Got match data: " + cacheId);

                matchStrings.add(matchString);

                mCache.addData(String.valueOf(matchID), matchString);

                MatchDto matchDto = new Gson().fromJson(matchString, MatchDto.class);
                matchDto.focusChamp = champion;
                matches.add(matchDto);
                inflationQueue.add(matchDto);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse matchlist: " + e);
        }
    }

    private void populateMatch(LayoutInflater inflater, LinearLayout parent, MatchDto match, int i) {
        Log.d(TAG, "populateMatch()");
        final int ii = i;
        ParticipantDto player = match.getFocusPlayerInfo();
        View view = inflater.inflate(R.layout.layout_match_preview, parent, false);
        TextView textWin = view.findViewById(R.id.playerName);
        if (player.stats.win) {
            textWin.setText("Win");
            view.setBackground(getDrawable(R.drawable.background5));
        } else {
            textWin.setText("Loss");
            view.setBackground(getDrawable(R.drawable.background4));
        }
        TextView textMode = view.findViewById(R.id.matchMode);
        setMode(textMode, match.queueId);
        TextView textLevel = view.findViewById(R.id.playerLevel);
        textLevel.setText(String.valueOf(player.stats.champLevel));
        TextView textDate = view.findViewById(R.id.matchDate);
        Date date = new Date(match.gameCreation);
        textDate.setText(date.toString());
        TextView textStats = view.findViewById(R.id.matchStats);
        setStats(textStats, player.stats.goldEarned, player.stats.kills, player.stats.deaths, player.stats.assists, match.gameDuration, player.stats.totalMinionsKilled);
        ImageView portrait = view.findViewById(R.id.playerChampPortrait);
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
        Long l = gameDuration;
        double time = l.doubleValue();
        int mins = (int) Math.floor(time/60);
        int secs = (int) gameDuration % 60;
        String sec = "";
        if (secs < 10) {
            sec = "0" + secs;
        } else {
            sec = String.valueOf(secs);
        }

        String ret = "Gold: " + gold + " | CS: " + cs + " | KDA: " + kills + "/"
                + deaths + "/" + assists + "\nGametime: " + mins + ":" + sec;
        textStats.setText(ret);
    }

    private void setSummSpellImages(View view, int spellID1, int spellID2) {
        ImageView ss1 = view.findViewById(R.id.matchSumm1);
        String spellName1 = mHelper.getSpellImgFromId(spellID1);
        String url1 = mRequestManager.getSpellImageURL(spellName1);
        setImg(ss1, url1);
        ImageView ss2 = view.findViewById(R.id.matchSumm2);
        String spellName2 = mHelper.getSpellImgFromId(spellID2);
        String url2 = mRequestManager.getSpellImageURL(spellName2);
        setImg(ss2, url2);
    }

    private void setItemImages(View view, ArrayList<Integer> items) {
        RelativeLayout relativeLayout = view.findViewById(R.id.matchItemLayout);
        ImageView img1 = relativeLayout.findViewById(R.id.matchItem1);
        if (items.get(0) != 0) {
            setItemimage(img1, items.get(0));
        }
        ImageView img2 = relativeLayout.findViewById(R.id.matchItem2);
        if (items.get(1) != 0) {
            setItemimage(img2, items.get(1));
        }
        ImageView img3 = relativeLayout.findViewById(R.id.matchItem3);
        if (items.get(2) != 0) {
            setItemimage(img3, items.get(2));
        }
        ImageView img4 = relativeLayout.findViewById(R.id.matchItem4);
        if (items.get(3) != 0) {
            setItemimage(img4, items.get(3));
        }
        ImageView img5 = relativeLayout.findViewById(R.id.matchItem5);
        if (items.get(4) != 0) {
            setItemimage(img5, items.get(4));
        }
        ImageView img6 = relativeLayout.findViewById(R.id.matchItem6);
        if (items.get(5) != 0) {
            setItemimage(img6, items.get(5));
        }
        ImageView img7 = relativeLayout.findViewById(R.id.matchItem7);
        if (items.get(6) != 0) {
            setItemimage(img7, items.get(6));
        }
    }

    private void setItemimage(ImageView img, int i) {
        if (i == 0) {
            img.setImageResource(R.drawable.empty_item);
            return;
        }
        String imgName = mHelper.getItemImgFromId(i);
        String url = mRequestManager.getItemImageURL(imgName);
        if (url == Constants.UNKNOWN_IMAGE) {}
        setImg(img, url);
    }

    private void setPortrait(ImageView portrait, int championID) {
        String imgName = mHelper.getChampionImgFromId(championID);
        String url = mRequestManager.getChampionImageURL(imgName);
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

    private void setImg(ImageView img, String url) {Glide.with(mContext).load(url).into(img);}
}
