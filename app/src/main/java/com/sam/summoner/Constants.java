package com.sam.summoner;

public interface Constants {
    int NORM_DRAFT_ID = 400;
    int RANKED_SOLO_ID = 420;
    int NORM_BLIND_ID = 430;
    int RANKED_FLEX_ID = 440;
    int ARAM_ID = 450;
    int NORM_BLIND_3S_ID = 460;
    int RANKED_3S_ID = 470;

    int MATCH_HISTORY_LENGTH = 10;

    // Database and Table Names
    String STATIC_DB_NAME = "static_data.db";
    String CHAMP_TABLE_NAME = "champion_table";
    String ITEM_TABLE_NAME = "item_table";
    String SS_TABLE_NAME = "summspell_table";

    String SUMMONER_DB_NAME = "summoner_data.db";
    String FRIENDS_TABLE_NAME = "friends_table";

    String UNKNOWN_IMAGE = "UNKNOWN";

    // Role names
    String ROLE_TOP = "TOP";
    String ROLE_JUNGLE = "JUNGLE";
    String ROLE_MID = "MIDDLE";
    String ROLE_ADC = "DUO_CARRY";
    String ROLE_SUPPORT = "DUO_SUPPORT";

    /*
        TODOLIST
        - Champ mastery/winrate section (InfoActivity)
        - Overall match history on main info page (InfoActivity)
        - Match history expansion (MatchHistoryActivity, InfoActivity)
        - Make a sweet logo for home page (MainActivity)
        - AccountInfo icon stuff
            - Have summoner icon beside summoner name in info page
            - Database for summoner icons
        - Favorites list
        - Runes
        - Tournament code search, send to full match view
        - On click full match view to expand for extra info
     */

}
