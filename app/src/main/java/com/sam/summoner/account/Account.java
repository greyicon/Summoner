package com.sam.summoner.account;

//Basic account information for a summoner, ie. not ranked or gameplay information
public class Account {
    private  int iconID;
    private String summonerName;
    private long summonerLevel;
    private long accountID;
    private long summonerID;

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
    public long getSummonerLevel() {
        return summonerLevel;
    }
    public long getAccountID() {
        return accountID;
    }
    public long getSummonerID() {
        return summonerID;
    }
}
