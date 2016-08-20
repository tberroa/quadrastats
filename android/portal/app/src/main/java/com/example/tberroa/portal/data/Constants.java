package com.example.tberroa.portal.data;

public class Constants {

    public static final String DIVISION_1 = "I";
    public static final String DIVISION_2 = "II";
    public static final String DIVISION_3 = "III";
    public static final String DIVISION_4 = "IV";
    public static final String DIVISION_5 = "V";
    public static final int ERR_FRIEND_ALREADY_LISTED = 50;
    public static final int ERR_FRIEND_EQUALS_USER = 55;
    public static final int ERR_FRIEND_LIMIT_REACHED = 60;
    public static final int ERR_INTERNAL_ERROR = 65;
    public static final int ERR_INVALID_CREDENTIALS = 70;
    public static final int ERR_INVALID_REQUEST_FORMAT = 75;
    public static final int ERR_INVALID_RIOT_RESPONSE = 80;
    public static final int ERR_SUMMONER_ALREADY_REGISTERED = 85;
    public static final int ERR_SUMMONER_DOES_NOT_EXIST = 90;
    public static final int ERR_SUMMONER_NOT_REGISTERED = 95;
    public static final String POS_BOT = "DUO_CARRY";
    public static final String POS_JUNGLE = "JUNGLE";
    public static final String POS_MID = "MIDDLE";
    public static final String POS_SUPPORT = "DUO_SUPPORT";
    public static final String POS_TOP = "TOP";
    public static final String REGION_BR = "br";
    public static final String REGION_EUNE = "eune";
    public static final String REGION_EUW = "euw";
    public static final String REGION_KR = "kr";
    public static final String REGION_LAN = "lan";
    public static final String REGION_LAS = "las";
    public static final String REGION_NA = "na";
    public static final String REGION_OCE = "oce";
    public static final String REGION_RU = "ru";
    public static final String REGION_TR = "tr";
    public static final char SERIES_LOSS = 'L';
    public static final char SERIES_UNPLAYED = 'N';
    public static final char SERIES_WIN = 'W';
    public static final String TAG_DEBUG = "TAG_DEBUG";
    public static final String TAG_EXCEPTIONS = "TAG_EXCEPTIONS";
    public static final String TIER_BRONZE = "BRONZE";
    public static final String TIER_CHALLENGER = "CHALLENGER";
    public static final String TIER_DIAMOND = "DIAMOND";
    public static final String TIER_GOLD = "GOLD";
    public static final String TIER_MASTER = "MASTER";
    public static final String TIER_PLAT = "PLATINUM";
    public static final String TIER_SILVER = "SILVER";
    public static final int UI_DIALOG_HEIGHT = 80;
    public static final int UI_DIALOG_WIDTH = 95;
    public static final String UI_NO_ITEM = "NO_ITEM";
    public static final String UI_RELOAD = "-80";
    public static final String URL_CHAMPION = "/img/champion/";
    public static final String URL_DATA_DRAGON = "http://ddragon.leagueoflegends.com/cdn/";
    public static final String URL_IMAGE_TYPE = ".png";
    public static final String URL_ITEM = "/img/item/";
    public static final String URL_MASTERY = "/img/mastery/";
    public static final String URL_PROFILE = "/img/profileicon/";
    public static final String URL_SS_BARRIER = "/img/spell/SummonerBarrier.png";
    public static final String URL_SS_CLEANSE = "/img/spell/SummonerBoost.png";
    public static final String URL_SS_EXHAUST = "/img/spell/SummonerExhaust.png";
    public static final String URL_SS_FLASH = "/img/spell/SummonerFlash.png";
    public static final String URL_SS_GHOST = "/img/spell/SummonerHaste.png";
    public static final String URL_SS_HEAL = "/img/spell/SummonerHeal.png";
    public static final String URL_SS_IGNITE = "/img/spell/SummonerDot.png";
    public static final String URL_SS_SMITE = "/img/spell/SummonerSmite.png";
    public static final String URL_SS_TELEPORT = "/img/spell/SummonerTeleport.png";
    public static final String URL_TRACEPOT = "https://collector.tracepot.com/dc940cee";
    public static final String VALID_CHANGE_EMAIL = "email";
    public static final String VALID_CHANGE_PASSWORD = "summoner_id";
    public static final String VALID_FRIEND_OP = "summoner_id";
    public static final String VALID_GET_CHAMPIONS = "version";
    public static final String VALID_GET_MATCH_STATS = "summoner_id";
    public static final String VALID_GET_SEASON_STATS = "summoner_id";
    public static final String VALID_GET_SUMMONERS = "summoner_id";
    public static final String VALID_REGISTER = "summoner_id";
    public static final String VALID_RESET_PASSWORD = "summoner_id";
    public static final String VALID_SIGN_IN = "summoner_id";
    private static final String URL_BASE = "http://52.90.34.48/";
    public static final String URL_ADD_FRIEND = URL_BASE + "summoners/add-friend.json";
    public static final String URL_CHANGE_EMAIL = URL_BASE + "summoners/change-email.json";
    public static final String URL_CHANGE_PASSWORD = URL_BASE + "summoners/change-password.json";
    public static final String URL_GET_SUMMONERS = URL_BASE + "summoners/get.json";
    public static final String URL_SIGN_IN = URL_BASE + "summoners/login.json";
    public static final String URL_REGISTER = URL_BASE + "summoners/register.json";
    public static final String URL_REMOVE_FRIEND = URL_BASE + "summoners/remove-friend.json";
    public static final String URL_RESET_PASSWORD = URL_BASE + "summoners/reset-password.json";
    public static final String URL_GET_MATCH_STATS = URL_BASE + "stats/match.json";
    public static final String URL_GET_SEASON_STATS = URL_BASE + "stats/season.json";
    private static final String URL_STATIC_BASE = "https://global.api.pvp.net/api/lol/static-data/na/v1.2";
    public static final String URL_GET_CHAMPIONS = URL_STATIC_BASE + "/champion?api_key=" + Keys.RIOT_API_KEY;

    private Constants() {
    }
}


