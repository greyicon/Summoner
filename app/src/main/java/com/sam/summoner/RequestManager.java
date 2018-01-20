package com.sam.summoner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

//Generates request URLs
public class RequestManager {
    private final String TAG = "RequestManager";
    private Context ctx;
    // ddragon version default, is updated on application launch
    private String ddVersion = "7.24.2";

    public RequestManager(Context context) {
        ctx = context;
    }

    public String getAccountJObject(String name) {
        Log.d(TAG, "Handling request: getAccountJObject...");
        String ret = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"
        + name + "?api_key=" + Constants.API_KEY;
        return getJsonData(ret);
    }

    public String getRankJArray(int id) {
        Log.d(TAG, "Handling request: getRankJArray...");
        String ret = "https://na1.api.riotgames.com/lol/league/v3/positions/by-summoner/"
                + id + "?api_key=" + Constants.API_KEY;
        return getJsonData(ret);
    }

    public String getMatchHistoryJObject(int aid, int queue) {
        Log.d(TAG, "Handling request: getMatchHistoryJObject...");
        String ret = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"
                + aid + "?queue=" + queue + "&api_key=" + Constants.API_KEY;
        return getJsonData(ret);
    }

    public String getChampions() {
        Log.d(TAG, "Handling request: getChampions...");
        String ret = "https://na1.api.riotgames.com/lol/static-data/v3/champions?locale=en_US&tags=image&dataById=false&api_key="
                + Constants.API_KEY;
        return getJsonData(ret);
    }

    public String getDdragonVersion() {
        Log.d(TAG, "Handling request: getDdragonVersion...");
        String ret = "https://ddragon.leagueoflegends.com/api/versions.json";
        return getJsonData(ret);
    }

    public void setDdVersion(String ver) {
        ddVersion = ver;
    }

    private String getJsonData(String url) {
        Log.d(TAG, "Getting JSON data...");
        String jString = null;
        try {
            jString = new WebGrabber(ctx).execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Failed to get JSON data: " + e);
            Toast.makeText(ctx, "Task failed", Toast.LENGTH_SHORT).show();
        }
        if (jString != null) {
            Log.d(TAG, "Got JSON data.");
        } else {
            Log.e(TAG, "Failed to get JSON data: jString is null");
        }
        return jString;
    }

}
