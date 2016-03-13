package com.example.tberroa.portal.data;

public class Params {

    private Params(){
    }

    // UI parameters
    static public final String RELOAD = "-80";

    // friends
    static public final int MAX_FRIENDS = 7;

    // broadcast
    static public final String SIGN_IN_SUCCESS = "sign_in_success";
    static public final String SIGN_IN_FAILED = "sign_in_failed";
    static public final String UPDATE_COMPLETE = "update_complete";

    // authentication
    static public final String SIGN_IN_URL = "http://portalapp.altervista.org/signin.php";
    static public final String REGISTER_URL = "http://portalapp.altervista.org/register.php";

    // network parameters
    static public final String POST_MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    // response codes, RC = riot code
    static public final String HTTP_GET_FAILED = "-50";
    static public final String RC_200_SUCCESS = "200";

    // riot api
    static public final String RIOT_API_BASE_URL = "https://na.api.pvp.net/api/lol/";
    static public final String API_SUMMONER = "/v1.4/summoner/";
    static public final String API_MATCHLIST = "/v2.2/matchlist/";
    static public final String API_MATCH = "/v2.2/match/";
    static public final String DATA_DRAGON_BASE_URL = "http://ddragon.leagueoflegends.com/cdn/";
    static public final String API_KEY = "f5ee63d2-54d8-466d-ae42-df491f8eabc5";

    // queue types
    static public final String DYNAMIC_QUEUE = "TEAM_BUILDER_DRAFT_RANKED_5x5";
    static public final String SOLO_QUEUE = "RANKED_SOLO_5x5";
    static public final String TEAM_3 = "RANKED_TEAM_3x3";
    static public final String TEAM_5 = "RANKED_TEAM_5x5";

    // seasons
    static public final String SEASON_2016 = "SEASON2016";

    // log tags
    static public final String TAG_EXCEPTIONS = "tag_exceptions";
    static public final String TAG_DEBUG = "tag_debug";
}
