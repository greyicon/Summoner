package com.sam.summoner.match;

import com.sam.summoner.Constants;

import java.util.ArrayList;

public class ParticipantDto {
    public ParticipantStatsDto stats;
    public int participantId;
    public int teamId;
    public int championId;
    //public String highestAchievedSeasonTier;
    public int spell1Id;
    public int spell2Id;
    public ParticipantTimelineDto timeline;
    public transient String playerRole = null;

    public String getRole() {
        if (this.playerRole != null) {
            return this.playerRole;
        }
        String playerRole = "";
        String lane = this.timeline.lane;
        String role = this.timeline.role;
        if (lane.equals("BOTTOM")) {
            playerRole = role;
        } else {
            playerRole = lane;
        }
        if (playerRole.equals("DUO")) {
            playerRole = Constants.ROLE_ADC;
        }
        this.playerRole = playerRole;
        return playerRole;
    }

    public void setRole(String role) {
        this.playerRole = role;
    }

    public ArrayList<Integer> getItems() {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (this.stats.item0 != 0) {
            ret.add(this.stats.item0);
        }
        if (this.stats.item1 != 0) {
            ret.add(this.stats.item1);
        }
        if (this.stats.item2 != 0) {
            ret.add(this.stats.item2);
        }
        if (this.stats.item3 != 0) {
            ret.add(this.stats.item3);
        }
        if (this.stats.item4 != 0) {
            ret.add(this.stats.item4);
        }
        if (this.stats.item5 != 0) {
            ret.add(this.stats.item5);
        }
        if (this.stats.item6 != 0) {
            ret.add(this.stats.item6);
        }
        while (ret.size() < 7) {
            ret.add(0);
        }
        return ret;
    }
}
