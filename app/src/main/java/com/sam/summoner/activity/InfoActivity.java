package com.sam.summoner.activity;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sam.summoner.Constants;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.account.AccountDto;
import com.sam.summoner.account.LeaguePositionDto;
import com.sam.summoner.account.SummonerDto;

public class InfoActivity extends AppCompatActivity {
    private final String TAG = "InfoActivity";

    private AccountDto accountDto;

    private RequestManager requestManager;
    private Gson gson;

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
        gson = new Gson();

        getAccountInformation(jString);

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
        updateRankedInformation();
    }

    private void getAccountInformation(String jString) {
        SummonerDto summonerDto = gson.fromJson(jString, SummonerDto.class);
        String rankedString = requestManager.getRankJArray(summonerDto.id);
        LeaguePositionDto[] positionDtos = gson.fromJson(rankedString, LeaguePositionDto[].class);
        applyAccountInformation(summonerDto, positionDtos);
    }

    private void applyAccountInformation(SummonerDto summonerDto, LeaguePositionDto[] positionDtos) {
        accountDto = new AccountDto();
        accountDto.summonerDto = summonerDto;
        for (int i = 0; i < positionDtos.length; i++) {
            String queueType = positionDtos[i].queueType;
            switch (queueType) {
                case "RANKED_SOLO_5x5":
                    accountDto.rankedSolo = positionDtos[i];
                    break;
                case "RANKED_FLEX_SR":
                    accountDto.rankedFlex = positionDtos[i];
                    break;
                case "RANKED_FLEX_TT":
                    accountDto.rankedTree = positionDtos[i];
                    break;
            }
        }
    }

    // refresh name and level in layout for current summoner
    private void updateNameView() {
        Log.d(TAG, "Updating name views...");
        String name = accountDto.summonerDto.name;
        long lvl = accountDto.summonerDto.summonerLevel;
        nameView.setText(name);
        levelView.setText("Level " + lvl);
    }

    // set text for summoner ranked queue information
    private void updateRankedInformation() {
        LinearLayout parent = (LinearLayout) findViewById(R.id.rankQueueLayout);
        parent.removeAllViews();
        inflateQueueView(accountDto.rankedSolo, Constants.RANKED_SOLO_ID);
        inflateQueueView(accountDto.rankedFlex, Constants.RANKED_FLEX_ID);
        inflateQueueView(accountDto.rankedTree, Constants.RANKED_3S_ID);
    }

    private void inflateQueueView(LeaguePositionDto positionDto, final int queueId) {
        LinearLayout parent = (LinearLayout) findViewById(R.id.rankQueueLayout);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_ranked_info, parent, false);

        if (positionDto == null) {
            handleNullPosition(view, parent, queueId);
            parent.addView(view);
            return;
        }

        ImageView img = (ImageView) view.findViewById(R.id.rankLogo);
        setImage(positionDto.tier, img);

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
                int id = queueId;
                viewMatchHistory(id);
            }
        });

        parent.addView(view);
    }

    private void setRankWinrate(TextView rankWinrate, LeaguePositionDto positionDto) {
        int wins = positionDto.wins;
        int losses = positionDto.losses;
        int total = wins + losses;
        int percent = (wins * 100) / total;
        rankWinrate.setText("Winrate: " + percent + "%");
    }

    private void setRankInfo(TextView rankInfo, LeaguePositionDto positionDto) {
        int wins = positionDto.wins;
        int losses = positionDto.losses;
        int lp = positionDto.leaguePoints;
        String info = "Wins: " + wins + " | Losses: " + losses + " | LP: " + lp;
        rankInfo.setText(info);
    }

    private void setRankTitle(TextView rankTitle, LeaguePositionDto positionDto) {
        String name = positionDto.leagueName;
        String tier = positionDto.tier;
        String rank = positionDto.rank;
        String title = tier + " " + rank + " - " + name;
        rankTitle.setText(title);
    }

    private void setRankLabel(TextView rankLabel, int queueId) {
        switch (queueId) {
            case Constants.RANKED_SOLO_ID:
                rankLabel.setText("Solo");
                break;
            case Constants.RANKED_FLEX_ID:
                rankLabel.setText("Flex");
                break;
            case Constants.RANKED_3S_ID:
                rankLabel.setText("Treeline");
                break;
        }
    }

    private void handleNullPosition(View view, LinearLayout parent, int queueId) {
        ImageView img = (ImageView) view.findViewById(R.id.rankLogo);
        setImage("", img);

        TextView rankLabel = (TextView) view.findViewById(R.id.rankLabel);
        setRankLabel(rankLabel, queueId);

        TextView rankTitle = (TextView) view.findViewById(R.id.rankTitle);
        rankTitle.setText(" ");

        TextView rankInfo = (TextView) view.findViewById(R.id.rankInfoPar);
        rankInfo.setText("No rank information available");

        TextView rankWinrate = (TextView) view.findViewById(R.id.rankWinrate);
        rankWinrate.setText(" ");

        ((ViewManager)view).removeView(view.findViewById(R.id.queueHistoryBtn));
    }

    // set emblems for ranked queues
    private void setImage(String tier, ImageView img) {
        Log.d(TAG, "Setting queue image: " + tier);
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

    // refresh the page for a new summoner search
    private void search() {
        Log.d(TAG, "Searching for a new summoner...");
        String summonerName = searchTxt.getText().toString();
        if (summonerName == "") {
            Log.d(TAG, "Search bar is empty.");
            return;
        }
        String jString = requestManager.getAccountJObject(summonerName);
        if (jString != null) {
            getAccountInformation(jString);
            updateNameView();
            updateRankedInformation();
        } else {
            Log.e(TAG, "Failed to find summoner: jString is null.");
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void viewMatchHistory(int queue) {
        Log.d(TAG, "Starting match history load for queue: " + queue);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            searchTxt.setText(data.getStringExtra("summName"));
            search();
        }
    }
}
