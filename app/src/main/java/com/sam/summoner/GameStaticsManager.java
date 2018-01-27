package com.sam.summoner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GameStaticsManager {
    private final String TAG = "GameStaticsManager";

    private LocalDatabaseHelper helper;
    private RequestManager requestManager;
    Context context;

    public GameStaticsManager(Context context) {
        helper = new LocalDatabaseHelper(context);
        requestManager = new RequestManager(context);
        loadChampionDatabase();
        loadItemDatabase();
        loadSpellDatabase();
    }

    private void loadChampionDatabase() {
        Log.d(TAG, "Checking if champions were already loaded...");
        int size = helper.getNumChampTableEntries();
        if (size > 0) {
            Log.d(TAG, "Champions already loaded, skipping.");
            return;
        } else {
            Log.d(TAG, "Champions not already loaded. Loading " + Constants.CHAMP_TABLE_NAME + "...");
        }
        String jString = requestManager.getChampions();
        if (jString != null) {
            try {
                JSONObject object = new JSONObject(jString);
                JSONObject data = object.getJSONObject("data");
                for (int i = 0; i < data.names().length(); i++) {
                    JSONObject champ = data.getJSONObject(data.names().getString(i));
                    String name = champ.getString("name");
                    int id = champ.getInt("key");
                    JSONObject image = champ.getJSONObject("image");
                    String img = image.getString("full");
                    helper.addChampion(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse champion data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load champion data: jString is empty.");
            Toast.makeText(context, "Failed to load champions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadItemDatabase() {
        Log.d(TAG, "Checking if items were already loaded...");
        int size = helper.getNumItemTableEntries();
        if (size > 0) {
            Log.d(TAG, "Items already loaded, skipping.");
            return;
        } else {
            Log.d(TAG, "Items not already loaded. Loading " + Constants.ITEM_TABLE_NAME + "...");
        }
        String jString = requestManager.getItems();
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
                    helper.addItem(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse item data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load item data: jString is empty.");
            Toast.makeText(context, "Failed to load item.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSpellDatabase() {
        Log.d(TAG, "Checking if spells were already loaded...");
        int size = helper.getNumSpellTableEntries();
        if (size > 0) {
            Log.d(TAG, "Spells already loaded, skipping.");
            return;
        } else {
            Log.d(TAG, "Spells not already loaded. Loading " + Constants.SS_TABLE_NAME + "...");
        }
        String jString = requestManager.getSummonerSpells();
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
                    helper.addSpell(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse spell data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load spell data: jString is empty.");
            Toast.makeText(context, "Failed to load spell.", Toast.LENGTH_SHORT).show();
        }
    }

}
