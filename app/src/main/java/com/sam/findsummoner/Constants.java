package com.sam.findsummoner;

public interface Constants {
    int NORM_DRAFT_ID = 400;
    int RANKED_SOLO_ID = 420;
    int NORM_BLIND_ID = 430;
    int RANKED_FLEX_ID = 440;
    int ARAM_ID = 450;
    int NORM_BLIND_3S_ID = 460;
    int RANKED_3S_ID = 470;

    int MATCH_HISTORY_LENGTH = 10;

    String EMPTY_ITEM_URL = "https://vectr.com/greyicon/g1FbTwdUw.svg";

    // Database and Table Names
    String STATIC_DB_NAME = "static_data.db";
    String CHAMP_TABLE_NAME = "champion_table";
    String ITEM_TABLE_NAME = "item_table";
    String SS_TABLE_NAME = "summspell_table";
    String FRIENDS_TABLE_NAME = "friends_table";
    String DD_TABLE_NAME = "ddragon_table";

    String DEFAULT_DD_VERSION = "8.4.1";

    String UNKNOWN_IMAGE = "UNKNOWN";

    // Role names
    String ROLE_TOP = "TOP";
    String ROLE_JUNGLE = "JUNGLE";
    String ROLE_MID = "MIDDLE";
    String ROLE_ADC = "DUO_CARRY";
    String ROLE_SUPPORT = "DUO_SUPPORT";

    // Graphs
    int CHART_DAMAGE = 0;
    int CHART_TANK = 1;
    int CHART_VISION = 2;

    /*
        TODOLIST
        - Champ mastery/winrate section (InfoActivity)
        - Match history expansion (MatchHistoryActivity, InfoActivity)
        - Make a sweet logo for home page (MainActivity)
        - Runes
        - Tournament code search, send to full match view
     */

}
