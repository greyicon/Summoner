package com.sam.findsummoner.match;

import com.sam.findsummoner.Constants;

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

        playerRole = discernRole(lane, role);

        this.playerRole = playerRole;
        return playerRole;
    }

    private String discernRole(String lane, String role) {
        // Participant's role (Legal values: DUO, NONE, SOLO, DUO_CARRY, DUO_SUPPORT)
        // Participant's lane (Legal values: MID, MIDDLE, TOP, JUNGLE, BOT, BOTTOM)
        if (lane.equals("NONE")) {return Constants.ROLE_ADC;}
        if (lane.equals("MID") || lane.equals("MIDDLE")) {return Constants.ROLE_MID;}
        if (lane.equals("TOP")) {return Constants.ROLE_TOP;}
        if (lane.equals("JUNGLE")) {return Constants.ROLE_JUNGLE;}
        if (lane.equals("BOT") || lane.equals("BOTTOM")) {
            if (role.equals("DUO_SUPPORT")) {return Constants.ROLE_SUPPORT;}
            else {return Constants.ROLE_ADC;}
        }
        return null;
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
