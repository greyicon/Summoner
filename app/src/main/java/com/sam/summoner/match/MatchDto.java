package com.sam.summoner.match;

import java.util.ArrayList;

public class MatchDto {
    //public int seasonId;
    public int queueId;
    public long gameId;
    public ArrayList<ParticipantIdentityDto> participantIdentities;
    //public String gameVersion;
    //public String platformId;
    //public String gameMode;
    //public int mapId;
    //public String gameType;
    public ArrayList<TeamStatsDto> teams;
    public ArrayList<ParticipantDto> participants;
    public long gameDuration;
    public long gameCreation;
    public transient int focusChamp;

    public int getWinner() {
        int ret = 0;
        for (TeamStatsDto team : teams) {
            if (team.win.equals("Win")) {
                ret =  team.teamId;
            }
        }
        return ret;
    }

    public ArrayList<ParticipantDto> getTeam(int teamId) {
        ArrayList<ParticipantDto> ret = new ArrayList<ParticipantDto>();
        for (ParticipantDto player : participants) {
            if (player.teamId == teamId) {
                ret.add(player);
            }
        }
        return ret;
    }

    public int getTeamKills(ArrayList<ParticipantDto> team) {
        int kills = 0;
        for (ParticipantDto player : team) {
            kills += player.stats.kills;
        }
        return kills;
    }

    public int getTeamDeaths(ArrayList<ParticipantDto> team) {
        int deaths = 0;
        for (ParticipantDto player : team) {
            deaths += player.stats.deaths;
        }
        return deaths;
    }

    public int getTeamAssists(ArrayList<ParticipantDto> team) {
        int assists = 0;
        for (ParticipantDto player : team) {
            assists += player.stats.assists;
        }
        return assists;
    }

    public int getTeamGold(ArrayList<ParticipantDto> team) {
        int gold = 0;
        for (ParticipantDto player : team) {
            gold += player.stats.goldEarned;
        }
        return gold;
    }


    public ParticipantDto getLaner(String role, ArrayList<ParticipantDto> team) {
        ParticipantDto ret = null;
        for (ParticipantDto player : team) {
            String thisRole = player.getRole();
            if (thisRole.equals(role)) {
                ret = player;
                break;
            }
        }
        return ret;
    }

    public String getSummonerNameFromPartId(int id) {
        String ret = "";
        for (ParticipantIdentityDto player : this.participantIdentities) {
            if (player.participantId == id) {
                ret = player.player.summonerName;
            }
        }
        return ret;
    }

    public ParticipantDto getFocusPlayerInfo() {
        ParticipantDto ret = null;
        for (ParticipantDto player : participants) {
            if (player.championId == focusChamp) {
                ret = player;
                break;
            }
        }
        return ret;
    }
}
