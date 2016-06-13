package com.example.tberroa.portal.data;

public class Params {

    // backend urls
    static public final String BURL_SIGN_IN = "http://52.90.34.48/summoners/login.json";
    static public final String BURL_REGISTER = "http://52.90.34.48/summoners/register.json";
    static public final String BURL_GET_SUMMONERS = "http://52.90.34.48/summoners/get.json";
    static public final String BURL_ADD_FRIEND = "http://52.90.34.48/summoners/add-friend.json";
    static public final String BURL_REMOVE_FRIEND = "http://52.90.34.48/summoners/remove-friend.json";
    static public final String BURL_MATCH_STATS = "http://52.90.34.48/stats/match.json";

    // user limits
    static public final int MAX_FRIENDS = 8;
    static public final int MAX_MATCHES = 10;

    // intents
    static public final String RELOAD = "-80";

    // log tags
    static public final String TAG_EXCEPTIONS = "tag_exceptions";
    static public final String TAG_DEBUG = "tag_debug";

    // network
    static public final String POST_MEDIA_TYPE = "application/json; charset=utf-8";

    // riot static api
    static public final String RURL_DATA_DRAGON = "http://ddragon.leagueoflegends.com/cdn/";
    static public final String RURL_PROFILE_ICON = "6.4.1/img/profileicon/";

    private Params() {
    }
}
