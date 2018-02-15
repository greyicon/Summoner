package com.sam.summoner.match;

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
        return playerRole;
    }

    public void setRole(String role) {
        this.playerRole = role;
    }

    public ArrayList<Integer> getItems() {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ret.add(this.stats.item1);
        ret.add(this.stats.item2);
        ret.add(this.stats.item3);
        ret.add(this.stats.item4);
        ret.add(this.stats.item5);
        ret.add(this.stats.item6);
        ret.add(this.stats.item7);
        return ret;
    }
}
