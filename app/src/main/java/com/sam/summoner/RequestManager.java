package com.sam.summoner;

//Generates request URLs
public class RequestManager {
    public RequestManager() {}

    public String getAccountJOBject(String name) {
        String ret = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"
        + name + "?api_key=" + Constants.API_KEY;
        return ret;
    }

    public String getRankJArray(int id) {
        String ret = "https://na1.api.riotgames.com/lol/league/v3/positions/by-summoner/"
                + id + "?api_key=" + Constants.API_KEY;
        return ret;
    }

    public String getMatchHistoryJObject(int aid, int queue) {
        String ret = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"
                + aid + "?queue=" + queue + "&api_key=" + Constants.API_KEY;
        return ret;
    }

    public String getChampions() {
        String ret = "https://na1.api.riotgames.com/lol/static-data/v3/champions?locale=en_US&tags=image&dataById=false&api_key="
                + Constants.API_KEY;
        return ret;
    }
}
