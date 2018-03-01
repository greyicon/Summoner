package com.sam.summoner;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GameStaticsManager {
    private final String TAG = "GameStaticsManager";

    private StaticsDatabaseHelper mHelper;
    private RequestManager mRequestManager;

    public GameStaticsManager(Context context) {
        mHelper = new StaticsDatabaseHelper(context);
        mRequestManager = RequestManager.getInstance();
    }

    public void init() {
        Log.d(TAG, "init()");
        loadChampionDatabase();
        loadItemDatabase();
        loadSpellDatabase();
    }

    private void loadChampionDatabase() {
        Log.d(TAG, "loadChampionDatabase()");
        String jString = mRequestManager.getChampions();
        if (jString != null) {
            try {
                JSONObject object = new JSONObject(jString);
                JSONObject data = object.getJSONObject("data");
                int numKeys = data.names().length();
                for (int i = 0; i < numKeys; i++) {
                    JSONObject champ = data.getJSONObject(data.names().getString(i));
                    String name = champ.getString("name");
                    int id = champ.getInt("key");
                    JSONObject image = champ.getJSONObject("image");
                    String img = image.getString("full");
                    mHelper.addChampion(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse champion data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load champion data: jString is empty.");
        }
    }

    private void loadItemDatabase() {
        Log.d(TAG, "loadItemDatabase()");
        String jString = mRequestManager.getItems();
        if (jString != null) {
            try {
                JSONObject object = new JSONObject(jString);
                JSONObject data = object.getJSONObject("data");
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject item = data.getJSONObject(key);
                    int id = Integer.parseInt(key);
                    String name = item.getString("name");
                    JSONObject image = item.getJSONObject("image");
                    String img = image.getString("full");
                    mHelper.addItem(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse item data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load item data: jString is empty.");
        }
    }

    private void loadSpellDatabase() {
        Log.d(TAG, "loadSpellDatabase()");
        String jString = mRequestManager.getSummonerSpells();
        if (jString != null) {
            try {
                JSONObject object = new JSONObject(jString);
                JSONObject data = object.getJSONObject("data");
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject spell = data.getJSONObject(key);
                    int id = Integer.parseInt(spell.getString("key"));
                    String name = spell.getString("name");
                    JSONObject image = spell.getJSONObject("image");
                    String img = image.getString("full");
                    mHelper.addSpell(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse spell data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load spell data: jString is empty.");
        }
    }

    public void clearStaticsTables() {
        Log.d(TAG, "clearStaticsTables()");
        mHelper.clearChampTable();
        mHelper.clearItemTable();
        mHelper.clearSpellTable();
    }
}
