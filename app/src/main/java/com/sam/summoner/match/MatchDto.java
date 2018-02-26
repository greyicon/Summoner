package com.sam.summoner.match;

import com.sam.summoner.Constants;

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
                player.getRole();
                ret.add(player);
            }
        }
        distributePlayers(ret);
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

    public ParticipantDto getLaner(String role, int teamId) {
        ParticipantDto ret = null;
        ArrayList<ParticipantDto> team = getTeam(teamId);
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

    // Redistribute player roles if there are duplicates
    //      ex: Two players are put mid in matchmaking
    private void distributePlayers(ArrayList<ParticipantDto> team) {
        int[] counts = {0,0,0,0,0};
        for (ParticipantDto player : team) {
            switch (player.getRole()) {
                case Constants.ROLE_TOP:
                    counts[0] += 1;
                    break;
                case Constants.ROLE_JUNGLE:
                    counts[1] += 1;
                    break;
                case Constants.ROLE_MID:
                    counts[2] += 1;
                    break;
                case Constants.ROLE_ADC:
                    counts[3] += 1;
                    break;
                case Constants.ROLE_SUPPORT:
                    counts[4] += 1;
                    break;
            }
        }
        for (int i = 0; i < 5; i++) {
            if (counts[i] == 0) {
                int j = getLargeIndex(counts);
                movePlayer(j, i, team);
                counts[j] -= 1;
                counts[i] += 1;
            }
        }
    }

    // Helper to move players from overpopulated roles into underpopulated ones
    private void movePlayer(int pos, int newPos, ArrayList<ParticipantDto> team) {
        ParticipantDto info = getPlayer(team, pos);
        switch (newPos) {
            case 0:
                info.setRole(Constants.ROLE_TOP);
                return;
            case 1:
                info.setRole(Constants.ROLE_JUNGLE);
                return;
            case 2:
                info.setRole(Constants.ROLE_MID);
                return;
            case 3:
                info.setRole(Constants.ROLE_ADC);
                return;
            case 4:
                info.setRole(Constants.ROLE_SUPPORT);
                return;
            default:
                return;
        }
    }

    private int getLargeIndex(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 1) {
                return i;
            }
        }
        return -1;
    }

    // Get a player from a team with a certain role
    private ParticipantDto getPlayer(ArrayList<ParticipantDto> team, int i) {
        switch (i) {
            case 0:
                return this.getLaner(Constants.ROLE_TOP, team);
            case 1:
                return this.getLaner(Constants.ROLE_JUNGLE, team);
            case 2:
                return this.getLaner(Constants.ROLE_MID, team);
            case 3:
                return this.getLaner(Constants.ROLE_ADC, team);
            case 4:
                return this.getLaner(Constants.ROLE_SUPPORT, team);
            default:
                return null;
        }
    }
}
