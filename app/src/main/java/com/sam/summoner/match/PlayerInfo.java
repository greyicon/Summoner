package com.sam.summoner.match;

import java.util.ArrayList;

public class PlayerInfo {
    private String summonerName;
    private int participantID;
    private int teamID;
    private String role;

    private boolean win;
    private int spellID1;
    private int spellID2;
    private int championID;
    private int champLevel;

    private ArrayList<Integer> items;

    private int gold;
    private int cs;
    private int kills;
    private int deaths;
    private int assists;

    public PlayerInfo(int partID) {
        participantID = partID;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public int getParticipantID() {
        return participantID;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public int getSpellID1() {
        return spellID1;
    }

    public void setSpellID1(int spellID1) {
        this.spellID1 = spellID1;
    }

    public int getSpellID2() {
        return spellID2;
    }

    public void setSpellID2(int spellID2) {
        this.spellID2 = spellID2;
    }

    public int getChampionID() {
        return championID;
    }

    public void setChampionID(int championID) {
        this.championID = championID;
    }

    public int getChampLevel() {
        return champLevel;
    }

    public void setChampLevel(int champLevel) {
        this.champLevel = champLevel;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public void setItems(ArrayList<Integer> items) {
        this.items = items;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public boolean getWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String cs) {
        this.role = cs;
    }
}
