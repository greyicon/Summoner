package com.sam.summoner.match;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

// Object for storing game data for a single match
public class Match {
    public static final String TAG = "Match";

    private long gameID;
    private int queueID;
    private long gameDuration;
    private long gameDate;
    private HashMap<Integer, PlayerInfo> players;

    private Integer focusChamp = null;
    private Integer focusPlayer = null;
    private Integer winningTeam = null;

    public Match(long matchID, int champID) {
        gameID = matchID;
        focusChamp = champID;
        winningTeam = -1;
        players = new HashMap<Integer, PlayerInfo>();
        // TODO
    }

    public long getGameID() {
        return gameID;
    }

    public int getQueueID() {
        return queueID;
    }

    public long getGameDate() {
        return gameDate;
    }

    public long getGameDuration() {
        return gameDuration;
    }

    // gets basic game data
    public void populateMatch(String jString) {
        Log.d(TAG, "Populating match...");
        try {
            JSONObject temp = new JSONObject(jString);
            int queueID = temp.getInt("queueId");
            this.queueID = queueID;
            long gameDuration = temp.getLong("gameDuration");
            this.gameDuration = gameDuration;
            long gameDate = temp.getLong("gameCreation");
            this.gameDate = gameDate;
            populatePlayers(jString);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse match data: " + e);
        }
        Log.d(TAG, "Match populated.");
    }

    // gets player account information and participantId's
    private void populatePlayers(String jString) {
        Log.d(TAG, "Populating player...");
        try {
            JSONObject temp = new JSONObject(jString);
            JSONArray array = temp.getJSONArray("participantIdentities");
            for (int i = 0; i < array.length(); i++) {
                JSONObject part = array.getJSONObject(i);
                int partID = part.getInt("participantId");
                JSONObject player = part.getJSONObject("player");
                String name = player.getString("summonerName");
                PlayerInfo pi = new PlayerInfo(partID);
                pi.setSummonerName(name);
                players.put(partID, pi);
            }
            populatePlayerGameInfo(temp);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse match data: " + e);
        }
        Log.d(TAG, "Player populated.");
    }

    // gets the rest of the player data
    private void populatePlayerGameInfo(JSONObject temp) {
        Log.d(TAG, "Getting player game info...");
        try {
            JSONArray array = temp.getJSONArray("participants");
            for (int i = 0; i < array.length(); i++) {
                JSONObject part = array.getJSONObject(i);
                int partID = part.getInt("participantId");
                PlayerInfo info = players.get(partID);
                int champ = part.getInt("championId");
                info.setChampionID(champ);
                if (champ == focusChamp) {
                    focusPlayer = partID;
                }
                info.setSpellID1(part.getInt("spell1Id"));
                info.setSpellID2(part.getInt("spell2Id"));
                info.setTeamID(part.getInt("teamId"));
                JSONObject stats = part.getJSONObject("stats");
                info.setWin(stats.getBoolean("win"));
                if (winningTeam == null && info.getWin()) {
                    winningTeam = info.getTeamID();
                }
                info.setKills(stats.getInt("kills"));
                info.setDeaths(stats.getInt("deaths"));
                info.setAssists(stats.getInt("assists"));
                info.setGold(stats.getInt("goldEarned"));
                ArrayList<Integer> items = new ArrayList<Integer>();
                items.add(stats.getInt("item0"));
                items.add(stats.getInt("item1"));
                items.add(stats.getInt("item2"));
                items.add(stats.getInt("item3"));
                items.add(stats.getInt("item4"));
                items.add(stats.getInt("item5"));
                items.add(stats.getInt("item6"));
                info.setItems(items);
                info.setChampLevel(stats.getInt("champLevel"));
                info.setCs(stats.getInt("totalMinionsKilled"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse match data: " + e);
        }
        Log.d(TAG, "Player info received.");
    }

    public PlayerInfo getFocusPlayerInfo() {
        return players.get(focusPlayer);
    }
}
