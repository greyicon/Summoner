package com.sam.summoner.activity;

import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.sam.summoner.Constants;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.StaticsDatabaseHelper;
import com.sam.summoner.match.MatchDto;
import com.sam.summoner.match.ParticipantDto;

import java.util.ArrayList;
import java.util.Observable;

public class MatchStatsActivity extends AppCompatActivity {
    private RequestManager requestManager;
    private Gson gson;
    private StaticsDatabaseHelper helper;
    private MatchDto matchDto;

    private RadioGroup selector;
    private HorizontalBarChart chartDamage;
    private ArrayList<BarEntry> valuesDmg;
    private HorizontalBarChart chartTank;
    private ArrayList<BarEntry> valuesTank;
    private HorizontalBarChart chartVision;
    private ArrayList<BarEntry> valuesVision;

    private HorizontalBarChart currentChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_stats);

        requestManager = RequestManager.getInstance();
        gson = new Gson();
        helper = new StaticsDatabaseHelper(this);
        matchDto = gson.fromJson(getIntent().getStringExtra("jString"), MatchDto.class);

        for (ParticipantDto part : matchDto.participants) {
            part.getRole();
        }

        int i = 0;
        RadioButton radDmg = (RadioButton) findViewById(R.id.radDmgDealt);
        radDmg.setId(i); i++;
        RadioButton radTank = (RadioButton) findViewById(R.id.radDmgTaken);
        radTank.setId(i); i++;
        RadioButton radVision = (RadioButton) findViewById(R.id.radVisionScore);
        radVision.setId(i);

        selector = (RadioGroup) findViewById(R.id.chartSelector);
        selector.check(0);
        selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                changeCharts(checkedId);
            }
        });

        chartDamage = (HorizontalBarChart) findViewById(R.id.chartDamage);
        valuesDmg = new ArrayList<>();
        chartTank = (HorizontalBarChart) findViewById(R.id.chartTank);
        valuesTank = new ArrayList<>();
        chartVision = (HorizontalBarChart) findViewById(R.id.chartVision);
        valuesVision = new ArrayList<>();
        chartDamage.setVisibility(View.VISIBLE);
        chartTank.setVisibility(View.GONE);
        chartVision.setVisibility(View.GONE);
        currentChart = chartDamage;

        setChartData();
        configureCharts(new HorizontalBarChart[]{chartDamage, chartTank, chartVision});
    }

    private void changeCharts(int checkedId) {
        switch (checkedId) {
            case 0:
                chartDamage.setVisibility(View.VISIBLE);
                currentChart.setVisibility(View.GONE);
                currentChart = chartDamage;
                break;
            case 1:
                chartTank.setVisibility(View.VISIBLE);
                currentChart.setVisibility(View.GONE);
                currentChart = chartTank;
                break;
            case 2:
                chartVision.setVisibility(View.VISIBLE);
                currentChart.setVisibility(View.GONE);
                currentChart = chartVision;
                break;
        }
    }

    private void configureCharts(HorizontalBarChart[] horizontalBarCharts) {
        for (HorizontalBarChart chart : horizontalBarCharts) {
            chart.setDragEnabled(false);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setScaleXEnabled(false);
            chart.setScaleYEnabled(false);
            chart.getAxisLeft().setDrawGridLines(true);
            chart.getAxisRight().setDrawGridLines(true);
            chart.getXAxis().setDrawGridLines(false);
            chart.animate().setDuration(500L);
            chart.getXAxis().setEnabled(false);
            chart.setFitBars(true);
            chart.invalidate();
            chart.getDescription().setEnabled(false);
        }
    }


    private void setChartData() {
        ArrayList<ParticipantDto> blueTeam = matchDto.getTeam(100);
        for (ParticipantDto part : blueTeam) {
            createBarEntries(part);
        }
        ArrayList<ParticipantDto> redTeam = matchDto.getTeam(200);
        for (ParticipantDto part : redTeam) {
            createBarEntries(part);
        }
        configureDataSets();
    }

    private void configureDataSets() {
        BarDataSet dmgSet = new BarDataSet(valuesDmg, "");
        dmgSet.setStackLabels(new String[] {"Physical", "Magic", "True"});
        dmgSet.setColors(Color.argb(255, 255, 171, 145), Color.argb(255, 206, 147, 216), Color.argb(255, 179, 229, 252));
        BarData dmgData = new BarData(dmgSet);
        dmgData.setBarWidth(0.85f);
        dmgData.setHighlightEnabled(false);
        dmgData.setDrawValues(false);
        chartDamage.setData(dmgData);

        BarDataSet tankSet = new BarDataSet(valuesTank, "");
        tankSet.setStackLabels(new String[] {"Physical", "Magic", "True"});
        tankSet.setColors(Color.argb(255, 255, 171, 145), Color.argb(255, 206, 147, 216), Color.argb(255, 179, 229, 252));
        BarData tankData = new BarData(tankSet);
        tankData.setBarWidth(0.85f);
        tankData.setHighlightEnabled(false);
        tankData.setDrawValues(false);
        chartTank.setData(tankData);

        BarDataSet visSet = new BarDataSet(valuesVision, "Vision Score");
        visSet.setColor(Color.argb(255, 147, 233, 171));
        BarData visData = new BarData(visSet);
        visData.setBarWidth(0.85f);
        visData.setHighlightEnabled(false);
        visData.setDrawValues(false);
        chartVision.setData(visData);
    }

    private void createBarEntries(ParticipantDto part) {
        Float pDmg  = new Float(part.stats.physicalDamageDealtToChampions);
        Float mDmg  = new Float(part.stats.magicDamageDealtToChampions);
        Float tDmg  = new Float(part.stats.trueDamageDealtToChampions);
        Float pTank = new Float(part.stats.physicalDamageTaken);
        Float mTank = new Float(part.stats.magicalDamageTaken);
        Float tTank = new Float(part.stats.trueDamageTaken);
        Float vis   = new Float(part.stats.visionScore);

        int index = getIndexFromPart(part);
        setChampIcon(9 - index, part);
        valuesDmg.add(new BarEntry(index, new float[] {pDmg,mDmg,tDmg}));
        valuesTank.add(new BarEntry(index, new float[] {pTank,mTank,tTank}));
        valuesVision.add(new BarEntry(index, vis));
    }

    private void setChampIcon(int i, ParticipantDto part) {
        ImageView img; int champId; String imgName; String url;
        switch (i) {
            case 0:
                img = (ImageView) findViewById(R.id.champ1);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 1:
                img = (ImageView) findViewById(R.id.champ2);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 2:
                img = (ImageView) findViewById(R.id.champ3);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 3:
                img = (ImageView) findViewById(R.id.champ4);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 4:
                img = (ImageView) findViewById(R.id.champ5);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 5:
                img = (ImageView) findViewById(R.id.champ6);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 6:
                img = (ImageView) findViewById(R.id.champ7);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 7:
                img = (ImageView) findViewById(R.id.champ8);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 8:
                img = (ImageView) findViewById(R.id.champ9);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
                return;
            case 9:
                img = (ImageView) findViewById(R.id.champ10);
                champId = part.championId;
                imgName = helper.getChampionImgFromId(champId);
                url = requestManager.getChampionImageURL(imgName);
                Glide.with(this).load(url).into(img);
        }
    }

    private int getIndexFromPart(ParticipantDto part) {
        int teamInt = getTeamInt(part);
        int roleInt = getRoleInt(part);
        return 9 - (teamInt + roleInt);
    }

    private int getRoleInt(ParticipantDto part) {
        switch (part.playerRole) {
            case Constants.ROLE_TOP:
                return 0;
            case Constants.ROLE_JUNGLE:
                return 1;
            case Constants.ROLE_MID:
                return 2;
            case Constants.ROLE_ADC:
                return 3;
            case Constants.ROLE_SUPPORT:
                return 4;
            default:
                return -1;
        }
    }

    private int getTeamInt(ParticipantDto part) {
        if (part.teamId == 100) {
            return 0;
        } else {
            return 5;
        }
    }
}
