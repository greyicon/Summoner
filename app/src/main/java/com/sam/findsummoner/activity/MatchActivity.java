package com.sam.findsummoner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sam.findsummoner.Constants;
import com.sam.findsummoner.R;
import com.sam.findsummoner.RequestManager;
import com.sam.findsummoner.StaticsDatabaseHelper;
import com.sam.findsummoner.match.MatchDto;
import com.sam.findsummoner.match.ParticipantDto;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {
    public static final String TAG = "MatchActivity";

    private RequestManager requestManager;
    private Gson gson;
    private StaticsDatabaseHelper helper;
    private MatchDto matchDto;

    private int winningTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        requestManager = RequestManager.getInstance();
        helper = new StaticsDatabaseHelper(this);

        gson = new Gson();

        final String jString = getIntent().getStringExtra("jString");

        matchDto = gson.fromJson(jString, MatchDto.class);

        winningTeam = matchDto.getWinner();

        formatHeaders();
        formatPlayers();

        Button graphsBtn = (Button) findViewById(R.id.graphsBtn);
        graphsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGraphs(jString);
            }
        });
    }

    private void gotoGraphs(String jString) {
        Intent i = new Intent(this, MatchStatsActivity.class);
        i.putExtra("jString", jString);
        startActivity(i);
    }

    // Set non-player UI information
    private void formatHeaders() {
        setWinnerText();
        setKDAs();
        setGolds();
    }

    // Set winner text
    private void setWinnerText() {
        if (winningTeam == 100) {
            TextView view = (TextView) findViewById(R.id.redTitle);
            view.setText("Red Team - LOSS");
        } else {
            TextView view = (TextView) findViewById(R.id.blueTitle);
            view.setText("Blue Team - LOSS");
        }
    }

    // Set team KDAs
    private void setKDAs() {
        ArrayList<ParticipantDto> blueTeam = matchDto.getTeam(100);
        int blueKills = matchDto.getTeamKills(blueTeam);
        int blueDeaths = matchDto.getTeamDeaths(blueTeam);
        int blueAssists = matchDto.getTeamAssists(blueTeam);

        ArrayList<ParticipantDto> redTeam = matchDto.getTeam(200);
        int redKills = matchDto.getTeamKills(redTeam);
        int redDeaths = matchDto.getTeamDeaths(redTeam);
        int redAssists = matchDto.getTeamAssists(redTeam);

        TextView blueView = (TextView) findViewById(R.id.blueKDA);
        blueView.setText("KDA: " + blueKills + "/" + blueDeaths + "/" + blueAssists);
        TextView redView = (TextView) findViewById(R.id.redKDA);
        redView.setText("KDA: " + redKills + "/" + redDeaths + "/" + redAssists);
    }

    // Set team golds
    private void setGolds() {
        ArrayList<ParticipantDto> blueTeam = matchDto.getTeam(100);
        int blueGold = matchDto.getTeamGold(blueTeam);

        ArrayList<ParticipantDto> redTeam = matchDto.getTeam(200);
        int redGold = matchDto.getTeamGold(redTeam);

        TextView blueView = (TextView) findViewById(R.id.blueGold);
        blueView.setText("Gold: " + blueGold);
        TextView redView = (TextView) findViewById(R.id.redGold);
        redView.setText("Gold: " + redGold);
    }

    // Inflate player lists
    private void formatPlayers() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout parent = (LinearLayout) findViewById(R.id.bluePlayers);
        ArrayList<ParticipantDto> blueTeam = matchDto.getTeam(100);
        for (int i = 0; i < 5; i++) {
            ParticipantDto player = getPlayer(blueTeam, i);
            View view = inflater.inflate(R.layout.layout_match, parent, false);
            setPlayerLayout(player, view);
            parent.addView(view);
        }
        parent = (LinearLayout) findViewById(R.id.redPlayers);
        ArrayList<ParticipantDto> redTeam = matchDto.getTeam(200);
        for (int i = 0; i < 5; i++) {
            ParticipantDto player = getPlayer(redTeam, i);
            View view = inflater.inflate(R.layout.layout_match, parent, false);
            setPlayerLayout(player, view);
            parent.addView(view);
        }
    }

    // Get a player from a team with a certain role
    private ParticipantDto getPlayer(ArrayList<ParticipantDto> team, int i) {
        switch (i) {
            case 0:
                return matchDto.getLaner(Constants.ROLE_TOP, team);
            case 1:
                return matchDto.getLaner(Constants.ROLE_JUNGLE, team);
            case 2:
                return matchDto.getLaner(Constants.ROLE_MID, team);
            case 3:
                return matchDto.getLaner(Constants.ROLE_ADC, team);
            case 4:
                return matchDto.getLaner(Constants.ROLE_SUPPORT, team);
            default:
                return null;
        }
    }

    // Use Glide to set ImageViews
    private void setImg(String url, ImageView view) {
        Glide.with(this).load(url).into(view);
    }

    // Format player specific information
    private void setPlayerLayout(final ParticipantDto player, View view) {
        int id = player.participantId;
        final String name = matchDto.getSummonerNameFromPartId(id);
        setBackground(player.teamId, view);
        TextView playerName = (TextView) view.findViewById(R.id.playerName);
        playerName.setText(name);
        TextView playerLevel = (TextView) view.findViewById(R.id.playerLevel);
        playerLevel.setText(String.valueOf(player.stats.champLevel));
        TextView playerStats = (TextView) view.findViewById(R.id.playerStats);
        playerStats.setText("Gold: " + player.stats.goldEarned + " | CS: " + player.stats.totalMinionsKilled + " | KDA: "
                + player.stats.kills + "/" + player.stats.deaths + "/" + player.stats.assists);
        setPlayerChamp(player.championId, view);
        setPlayerItems(player.getItems(), view);
        String summ1url = requestManager.getSpellImageURL(helper.getSpellImgFromId(player.spell1Id));
        setImg(summ1url, (ImageView) view.findViewById(R.id.playerSumm1));
        String summ2url = requestManager.getSpellImageURL(helper.getSpellImgFromId(player.spell2Id));
        setImg(summ2url, (ImageView) view.findViewById(R.id.playerSumm2));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(name);
            }
        });
    }

    // Set background color based on team
    private void setBackground(int team, View view) {
        if (team == 100) {
            view.setBackground(getDrawable(R.drawable.background3));
        } else {
            view.setBackground(getDrawable(R.drawable.background4));
        }
    }

    // Set player champ portrait
    private void setPlayerChamp(int champ, View view) {
        String imgName = helper.getChampionImgFromId(champ);
        ImageView img = (ImageView) view.findViewById(R.id.playerChampPortrait);
        String url = requestManager.getChampionImageURL(imgName);
        setImg(url, img);
    }

    // Set item layout
    private void setPlayerItems(List<Integer> items, View view) {
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.playerItemLayout);
        ImageView img1 = (ImageView) relativeLayout.findViewById(R.id.playerItem1);
        if (items.get(0) != 0) {
            setItem(items.get(0), img1);
        }
        ImageView img2 = (ImageView) relativeLayout.findViewById(R.id.playerItem2);
        if (items.get(1) != 0) {
            setItem(items.get(1), img2);
        }
        ImageView img3 = (ImageView) relativeLayout.findViewById(R.id.playerItem3);
        if (items.get(2) != 0) {
            setItem(items.get(2), img3);
        }
        ImageView img4 = (ImageView) relativeLayout.findViewById(R.id.playerItem4);
        if (items.get(3) != 0) {
            setItem(items.get(3), img4);
        }
        ImageView img5 = (ImageView) relativeLayout.findViewById(R.id.playerItem5);
        if (items.get(4) != 0) {
            setItem(items.get(4), img5);
        }
        ImageView img6 = (ImageView) relativeLayout.findViewById(R.id.playerItem6);
        if (items.get(5) != 0) {
            setItem(items.get(5), img6);
        }
        ImageView img7 = (ImageView) relativeLayout.findViewById(R.id.playerItem7);
        if (items.get(6) != 0) {
            setItem(items.get(6), img7);
        }
    }

    private void setItem(int i, ImageView view) {
        if (i == 0) {
            setImg(Constants.EMPTY_ITEM_URL, view);
            return;
        }
        String url = requestManager.getItemImageURL(helper.getItemImgFromId(i));
        if (url == Constants.UNKNOWN_IMAGE) {}
        setImg(url, view);
    }

    private void search(String name) {
        Intent i = new Intent();
        i.putExtra("summName", name);
        setResult(RESULT_OK, i);
        finish();
    }
}
