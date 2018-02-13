package com.sam.summoner;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sam.summoner.grabber.ImageGrabber;
import com.sam.summoner.grabber.WebGrabber;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

// Handles URL requests by generating URLs and downloading content
public class RequestManager {
    private static RequestManager instance = null;
    public static RequestManager getInstance() {
        if (instance == null) {instance = new RequestManager();}
        return instance;
    }

    private String API_KEY = Constants.API_KEY;
    private final String TAG = "RequestManager";
    // ddragon version default, is updated on application launch
    private String ddVersion = "7.24.2";

    private RequestManager() {}

    // JSON: Basic account information and constants from summoner name
    public String getAccountJObject(String name) {
        Log.d(TAG, "Handling request: getAccountJObject: " + name + "...");
        String ret = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"
        + name + "?api_key=" + API_KEY;
        return getJsonData(ret);
    }

    // JSON: Standings in all three ranked queues
    public String getRankJArray(long id) {
        Log.d(TAG, "Handling request: getRankJArray...");
        String ret = "https://na1.api.riotgames.com/lol/league/v3/positions/by-summoner/"
                + id + "?api_key=" + API_KEY;
        return getJsonData(ret);
    }

    // JSON: endIndex most recent games in given queue
    public String getMatchHistoryJObject(long aid, int queue, int numGames) {
        Log.d(TAG, "Handling request: getMatchHistoryJObject...");
        String ret = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/" +
                + aid + "?queue=" + queue + "&endIndex=" + numGames + "&api_key=" + API_KEY;
        return getJsonData(ret);
    }

    // JSON: Basic information from account's 20 most recent games
    public String getRecentMatchesJObject(long aid) {
        Log.d(TAG, "Handling request: getRecentMatchesJObject...");
        String ret = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"
                + aid + "/recent?api_key=" + API_KEY;
        return getJsonData(ret);
    }

    public String getChampions() {
        Log.d(TAG, "Handling request: getChampions...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/data/en_US/champion.json";
        return getJsonData(ret);
    }

    public String getItems() {
        Log.d(TAG, "Handling request: getItems...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/data/en_US/item.json ";
        return getJsonData(ret);
    }

    public String getSummonerSpells() {
        Log.d(TAG, "Handling request: getSummonerSpells...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/data/en_US/summoner.json";
        return getJsonData(ret);
    }

    public String getMatchData(long matchID) {
        Log.d(TAG, "Handling request: getMatchData: " + matchID + "...");
        String ret = "https://na1.api.riotgames.com/lol/match/v3/matches/" + matchID
                + "?api_key=" + API_KEY;
        return getJsonData(ret);
    }

    public String getDdragonVersion() {
        Log.d(TAG, "Handling request: getDdragonVersion...");
        String ret = "https://ddragon.leagueoflegends.com/api/versions.json";
        return getJsonData(ret);
    }

    public String getChampionImageURL(String imgName) {
        Log.d(TAG, "Handling request: getChampionImage: " + imgName + "...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/img/champion/" + imgName;
        return ret;
    }

    public String getItemImageURL(String imgName) {
        Log.d(TAG, "Handling request: getItemImage: " + imgName + "...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/img/item/" + imgName;
        return ret;
    }

    public String getSpellImageURL(String imgName) {
        Log.d(TAG, "Handling request: getSpellImage: " + imgName + "...");
        String ret = "http://ddragon.leagueoflegends.com/cdn/" + ddVersion + "/img/spell/" + imgName;
        return ret;
    }

    public void updateDdVersion() {
        Log.d(TAG, "Loading latest ddragon version code...");
        String ret = null;
        String jString = getDdragonVersion();
        if (jString != null) {
            try {
                JSONArray jArray = new JSONArray(jString);
                ret = jArray.getString(0);
                Log.d(TAG, "Ddragon version loaded.");
            } catch (JSONException e) {
                Log.e(TAG, "JSON error when loading ddragon version: " + e);
            }
        }
        ddVersion = ret;
    }

    public void setApiKey(String key) {
        API_KEY = key;
    }

    private String getJsonData(String url) {
        Log.d(TAG, "Getting JSON data...");
        String jString = null;
        try {
            jString = new WebGrabber().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Failed to get JSON data: " + e);
        }
        if (jString != null) {
            Log.d(TAG, "Got JSON data.");
        } else {
            Log.e(TAG, "Failed to get JSON data: jString is null");
        }
        return jString;
    }
}
