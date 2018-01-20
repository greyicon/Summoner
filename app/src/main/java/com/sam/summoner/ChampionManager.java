package com.sam.summoner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChampionManager {
    private final String TAG = "ChampionManager";

    private ChampionLocalDatabaseHelper helper;
    private RequestManager requestManager;
    Context context;

    public ChampionManager(Context context) {
        helper = new ChampionLocalDatabaseHelper(context);
        requestManager = new RequestManager(context);
        loadDatabase();
    }

    private void loadDatabase() {
        Log.d(TAG, "Loading champions.db...");
        String jString = requestManager.getChampions();
        if (jString != null) {
            try {
                JSONObject object = new JSONObject(jString);
                JSONObject data = object.getJSONObject("data");
                for (int i = 0; i < data.names().length(); i++) {
                    JSONObject champ = data.getJSONObject(data.names().getString(i));
                    String name = champ.getString("name");
                    int id = champ.getInt("id");
                    JSONObject image = champ.getJSONObject("image");
                    String img = image.getString("full");
                    helper.add(id, name, img);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse champion data: " + e);
            }
        } else {
            Log.e(TAG, "Failed to load champion data: jString is empty.");
            Toast.makeText(context, "Failed to load champions.", Toast.LENGTH_SHORT).show();
        }
    }
}
