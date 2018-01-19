package com.sam.summoner;

//Contains ladder information for a specified ranked queue for a summoner
public class RankedInfo {
    private String queue;
    private String leagueName;
    private String leagueTier;
    private String leagueRank;
    private int leaguePoints;
    private int wins;
    private int losses;

    public RankedInfo(String queue, String leagueName, String leagueTier, String leagueRank, int leaguePoints, int wins, int losses) {
        this.queue = queue;
        this.leagueName = leagueName;
        this.leagueTier = leagueTier;
        this.leagueRank = leagueRank;
        this.leaguePoints = leaguePoints;
        this.wins = wins;
        this.losses = losses;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getLeagueTier() {
        return leagueTier;
    }

    public void setLeagueTier(String leagueTier) {
        this.leagueTier = leagueTier;
    }

    public String getLeagueRank() {
        return leagueRank;
    }

    public void setLeagueRank(String leagueRank) {
        this.leagueRank = leagueRank;
    }

    public int getLeaguePoints() {
        return leaguePoints;
    }

    public void setLeaguePoints(int leaguePoints) {
        this.leaguePoints = leaguePoints;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
