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
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sam.summoner.Constants;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.StaticsDatabaseHelper;
import com.sam.summoner.account.AccountDto;
import com.sam.summoner.account.LeaguePositionDto;
import com.sam.summoner.account.SummonerDto;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {
    private final String TAG = "InfoActivity";
    private final Context mContext = this;

    private AccountDto accountDto;

    private RequestManager requestManager;
    private StaticsDatabaseHelper helper;
    private Gson gson;

    private TextView searchTxt;
    private Button addFavBtn;
    private TextView nameView;
    private TextView levelView;

    private String jString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        jString = getIntent().getStringExtra("jString");

        new LoadUI().execute();
    }

    private class LoadUI extends AsyncTask<Void, Void, Void> {
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
        protected Void doInBackground(Void... params) {
            Log.d(TAG + TAG_SUFFIX, "doInBackground()");
            initBackEnd();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG + TAG_SUFFIX, "onPostExecute()");
            initFrontEnd();
            if (dialog.isShowing()) {dialog.dismiss();}
        }
    }

    private void initBackEnd() {
        Log.d(TAG, "initBackEnd()");
        requestManager = RequestManager.getInstance();
        helper = new StaticsDatabaseHelper(this);
        gson = new Gson();
        getAccountInformation(jString);
    }

    private void initFrontEnd() {
        Log.d(TAG, "initFrontEnd()");
        nameView = (TextView) findViewById(R.id.nameView);
        levelView = (TextView) findViewById(R.id.levelView);
        setNameViewsByAccount();

        searchTxt = (TextView) findViewById(R.id.searchTxt);
        Button searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = searchTxt.getText().toString();
                search(name);
            }
        });

        addFavBtn = (Button) findViewById(R.id.addFavBtn);
        addFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        setButtonText();

        inflateQueueViewsByAccount();
    }

    private void getAccountInformation(String jString) {
        Log.d(TAG, "getAccountInformation()");
        SummonerDto summonerDto = gson.fromJson(jString, SummonerDto.class);
        String rankedString = requestManager.getRankJArray(summonerDto.id);
        LeaguePositionDto[] positionDtos = gson.fromJson(rankedString, LeaguePositionDto[].class);
        applyAccountInformation(summonerDto, positionDtos);
    }

    private void applyAccountInformation(SummonerDto summonerDto, LeaguePositionDto[] positionDtos) {
        Log.d(TAG, "applyAccountInformation()");
        accountDto = new AccountDto();
        accountDto.summonerDto = summonerDto;
        for (LeaguePositionDto positionDto : positionDtos) {
            String queueType = positionDto.queueType;
            switch (queueType) {
                case "RANKED_SOLO_5x5":
                    accountDto.rankedSolo = positionDto;
                    break;
                case "RANKED_FLEX_SR":
                    accountDto.rankedFlex = positionDto;
                    break;
                case "RANKED_FLEX_TT":
                    accountDto.rankedTree = positionDto;
                    break;
            }
        }
    }

    private void setNameViewsByAccount() {
        Log.d(TAG, "setNameViewsByAccount()");
        String name = accountDto.summonerDto.name;
        long lvl = accountDto.summonerDto.summonerLevel;
        nameView.setText(name);
        levelView.setText("Level " + lvl);

        // Set summoner icon
        ImageView img = (ImageView) findViewById(R.id.summonerIcon);
        String url = requestManager.getSummonerIconImageURL(accountDto.summonerDto.profileIconId);
        Glide.with(this).load(url).into(img);
    }

    private void inflateQueueViewsByAccount() {
        Log.d(TAG, "inflateQueueViewsByAccount()");
        LinearLayout parent = (LinearLayout) findViewById(R.id.rankQueueLayout);
        parent.removeAllViews();
        inflateQueueView(accountDto.rankedSolo, Constants.RANKED_SOLO_ID);
        inflateQueueView(accountDto.rankedFlex, Constants.RANKED_FLEX_ID);
        inflateQueueView(accountDto.rankedTree, Constants.RANKED_3S_ID);
    }

    private void inflateQueueView(LeaguePositionDto positionDto, final int queueId) {
        Log.d(TAG, "inflateQueueViewsByAccount(" + queueId + ")");
        LinearLayout parent = (LinearLayout) findViewById(R.id.rankQueueLayout);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_ranked_info, parent, false);

        if (positionDto == null) {
            handleNullPosition(view, queueId);
            parent.addView(view);
            return;
        }

        ImageView img = (ImageView) view.findViewById(R.id.rankLogo);
        setTierIcon(positionDto.tier, img);

        TextView rankLabel = (TextView) view.findViewById(R.id.rankLabel);
        setRankLabel(rankLabel, queueId);

        TextView rankTitle = (TextView) view.findViewById(R.id.rankTitle);
        setRankTitle(rankTitle, positionDto);

        TextView rankInfo = (TextView) view.findViewById(R.id.rankInfoPar);
        setRankInfo(rankInfo, positionDto);

        TextView rankWinrate = (TextView) view.findViewById(R.id.rankWinrate);
        setRankWinrate(rankWinrate, positionDto);

        Button rankHistoryBtn = (Button) view.findViewById(R.id.queueHistoryBtn);
        rankHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMatchHistory(queueId);
            }
        });

        parent.addView(view);
    }

    private void setTierIcon(String tier, ImageView img) {
        Log.d(TAG, "setTierIcon(" + tier + ")");
        switch (tier) {
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
            default:
                img.setImageResource(R.drawable.provisional);
        }
    }

    private void setRankLabel(TextView rankLabel, int queueId) {
        Log.d(TAG, "setRankLabel()");
        switch (queueId) {
            case Constants.RANKED_SOLO_ID:
                rankLabel.setText(R.string.rank_label_solo);
                break;
            case Constants.RANKED_FLEX_ID:
                rankLabel.setText(R.string.rank_label_flex);
                break;
            case Constants.RANKED_3S_ID:
                rankLabel.setText(R.string.rank_label_tree);
                break;
        }
    }

    private void setRankTitle(TextView rankTitle, LeaguePositionDto positionDto) {
        Log.d(TAG, "setRankTitle()");
        String name = positionDto.leagueName;
        String tier = positionDto.tier;
        String rank = positionDto.rank;
        String title = tier + " " + rank + " - " + name;
        rankTitle.setText(title);
    }

    private void setRankInfo(TextView rankInfo, LeaguePositionDto positionDto) {
        Log.d(TAG, "setRankInfo()");
        int wins = positionDto.wins;
        int losses = positionDto.losses;
        int lp = positionDto.leaguePoints;
        String info = "Wins: " + wins + " | Losses: " + losses + " | LP: " + lp;
        rankInfo.setText(info);
    }

    private void setRankWinrate(TextView rankWinrate, LeaguePositionDto positionDto) {
        Log.d(TAG, "setRankWinrate()");
        int wins = positionDto.wins;
        int losses = positionDto.losses;
        int total = wins + losses;
        int percent = (wins * 100) / total;
        rankWinrate.setText("Winrate: " + percent + "%");
    }

    private void handleNullPosition(View view, int queueId) {
        Log.d(TAG, "handleNullPosition(" + queueId + ")");
        ImageView img = (ImageView) view.findViewById(R.id.rankLogo);
        setTierIcon("", img);

        TextView rankLabel = (TextView) view.findViewById(R.id.rankLabel);
        setRankLabel(rankLabel, queueId);

        TextView rankTitle = (TextView) view.findViewById(R.id.rankTitle);
        rankTitle.setText(" ");

        TextView rankInfo = (TextView) view.findViewById(R.id.rankInfoPar);
        rankInfo.setText(R.string.rank_null_position);

        TextView rankWinrate = (TextView) view.findViewById(R.id.rankWinrate);
        rankWinrate.setText(" ");

        ((ViewManager)view).removeView(view.findViewById(R.id.queueHistoryBtn));
    }

    private void search(String name) {
        Log.d(TAG, "search(" + name + ")");
        if (name.equals("")) {
            Log.d(TAG, "Search bar is empty.");
            return;
        }
        new ReloadUI().execute(name);
    }

    private class ReloadUI extends AsyncTask<String, Void, Boolean> {
        ProgressDialog dialog;
        private final String TAG_SUFFIX = ".ReloadUI";

        @Override
        protected void onPreExecute() {
            Log.d(TAG + TAG_SUFFIX, "onPreExecute()");
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG + TAG_SUFFIX, "doInBackground()");
            String name = params[0];
            return reloadBackEnd(name);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            Log.d(TAG + TAG_SUFFIX, "onPostExecute(" + bool + ")");
            if (bool) {reloadFrontEnd();}
            if (dialog.isShowing()) {dialog.dismiss();}
        }
    }

    private boolean reloadBackEnd(String name) {
        String jString = requestManager.getAccountJObject(name);
        if (jString != null) {
            getAccountInformation(jString);
            return true;
        } else {
            Log.e(TAG, "Failed to find summoner: jString is null.");
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void reloadFrontEnd() {
        setNameViewsByAccount();
        inflateQueueViewsByAccount();
        setButtonText();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void toggleFavorite() {
        Log.d(TAG, "toggleFavorite()");
        String name = accountDto.summonerDto.name;
        ArrayList<String> favoriteList = helper.getFriends();
        if (favoriteList.contains(name)) {
            helper.removeFriend(name);
            Toast.makeText(this, "Favorite removed", Toast.LENGTH_SHORT).show();
        } else {
            helper.addFriend(name);
            Toast.makeText(this, "Favorite added", Toast.LENGTH_SHORT).show();
        }
        setButtonText();
    }

    private void setButtonText() {
        Log.d(TAG, "setButtonText()");
        String name = accountDto.summonerDto.name;
        ArrayList<String> favoriteList = helper.getFriends();
        if (favoriteList.contains(name)) {
            addFavBtn.setText(R.string.fav_remove);
        } else {
            addFavBtn.setText(R.string.fav_add);
        }
    }

    private void viewMatchHistory(int queue) {
        Log.d(TAG, "viewMatchHistory(" + queue + ")");
        String jString = requestManager.getMatchHistoryJObject(accountDto.summonerDto.accountId, queue, Constants.MATCH_HISTORY_LENGTH);
        if (jString != null) {
            Intent i = new Intent(this, MatchHistoryActivity.class);
            i.putExtra("jString", jString);
            startActivityForResult(i, 1);
        } else {
            Log.e(TAG, "Failed to load match history: jString is null.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(resultCode = " + resultCode + ")");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String name = data.getStringExtra("summName");
            search(name);
        }
    }
}
