package com.sam.summoner;

import java.util.ArrayList;

public class FavoriteList {
    private static FavoriteList instance = null;
    public static FavoriteList getInstance() {
        if (instance == null) {instance = new FavoriteList();}
        return instance;
    }

    private final int MAX_SIZE = 50;
    private ArrayList<String> list;

    private FavoriteList() {
        list = new ArrayList<String>();
    }

    public ArrayList<String> getFavoriteList() {
        return list;
    }

    public int addFavorite(String name) {
        if (list.size() < 50) {
            list.add(name);
            return 0;
        } else {
            return -1;
        }
    }

    public void removeFavorite(String name) {
        list.remove(name);
    }

    public boolean containsName(String name) {
        return list.contains(name);
    }
}
