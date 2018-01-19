package com.sam.summoner;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//deprecated?
public class ChampionManager {
    private Map<Integer, ChampionInfo> champions;
    private LocalChampionDatabaseHelper helper;
    Context context;

    public ChampionManager(Context context) {
        champions = new HashMap<Integer, ChampionInfo>();
        helper = new LocalChampionDatabaseHelper(context);
        loadChampions();
    }

    private class ChampionInfo {
        private String name;
        private int id;
        private String img;

        public ChampionInfo(String name, int id, String img) {
            this.name = name;
            this.id = id;
            this.img = img;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getImg() {
            return img;
        }
    }

    private void addChampion(int id, String name, String img) {
        champions.put(id, new ChampionInfo(name, id, img));
    }

    public ChampionInfo getChampionFromId(int id) {
        return champions.get(id);
    }

    private void loadChampions() {
        WebGrabber grabber = new WebGrabber(context);
        RequestManager requestManager = new RequestManager();
        String url = requestManager.getChampions();
        String jString = null;
        try {
            jString = new WebGrabber(context).execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(context, "Task failed", Toast.LENGTH_SHORT).show();
        }
        if (jString != null) {
            parseChampions(jString);
        } else {
            Toast.makeText(context, "Failed to load champions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseChampions(String jString) {
        try {
            JSONObject object = new JSONObject(jString);
            JSONObject data = object.getJSONObject("data");
            for (int i = 0; i < data.names().length(); i++) {
                JSONObject champ = data.getJSONObject(data.names().getString(i));
                String name = champ.getString("name");
                int id = champ.getInt("id");
                JSONObject image = champ.getJSONObject("image");
                String img = image.getString("full");
                addChampion(id, name, img);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
