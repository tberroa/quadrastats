package com.example.tberroa.portal.data;

public class Params {

    private Params(){
    }

    // database
    public static final String LOCAL_DB_NAME = "Portal.db";

    // UI parameters
    public static final String RELOAD = "-80";

    // authentication
    public static final String SIGN_IN_URL = "http://portalapp.altervista.org/signin.php";
    public static final String REGISTER_URL = "http://portalapp.altervista.org/register.php";

    // network parameters
    final public static String POST_MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    // response codes, RC = riot code
    public static final String HTTP_GET_FAILED = "-50";
    public static final String RC_200_SUCCESS = "200";

    // riot api
    public static final String RIOT_API_BASE_URL = "https://na.api.pvp.net/api/lol/";
    public static final String API_SUMMONER = "/v1.4/summoner/";
    public static final String API_MATCHLIST = "/v2.2/matchlist/";
    public static final String DATA_DRAGON_BASE_URL = "http://ddragon.leagueoflegends.com/cdn/";
    public static final String API_KEY = "f5ee63d2-54d8-466d-ae42-df491f8eabc5";

    // queue types
    public static final String TEAM_BUILDER_DRAFT_RANKED_5 = "TEAM_BUILDER_DRAFT_RANKED_5x5";
    public static final String RANKED_SOLO_5 = "RANKED_SOLO_5x5";
    public static final String RANKED_TEAM_3 = "RANKED_TEAM_3x3";
    public static final String RANKED_TEAM_5 = "RANKED_TEAM_5x5";

    // seasons
    public static final String PRESEASON_3 = "PRESEASON3";
    public static final String SEASON_3 = "SEASON3";
    public static final String PRESEASON_2014 = "PRESEASON2014";
    public static final String SEASON_2014 = "SEASON2014";
    public static final String PRESEASON_2015 = "PRESEASON2015";
    public static final String SEASON_2015 = "SEASON2015";
    public static final String PRESEASON_2016 = "PRESEASON2016";
    public static final String SEASON_2016 = "SEASON2016";

    // log tags
    public static final String TAG_EXCEPTIONS = "tag_exceptions";
    public static final String TAG_DEBUG = "tag_debug";
}
