package com.sam.summoner;

//Basic account information for a summoner, ie. not ranked or gameplay information
public class Account {
    private  int iconID;
    private String summonerName;
    private int summonerLevel;
    private int accountID;
    private int summonerID;

    public Account(int icon, String name, int level, int sid, int aid) {
        iconID = icon;
        summonerName = name;
        summonerLevel = level;
        accountID = aid;
        summonerID = sid;
    }

    public int getIconID() {
        return iconID;
    }
    public String getSummonerName() {
        return summonerName;
    }
    public int getSummonerLevel() {
        return summonerLevel;
    }
    public int getAccountID() {
        return accountID;
    }
    public int getSummonerID() {
        return summonerID;
    }
}
