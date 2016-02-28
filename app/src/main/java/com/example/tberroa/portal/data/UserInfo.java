package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String REGION = "region";
    private final String SUMMONER_NAME = "summoner_name";
    private final String IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public String getRegion(Context context){
        return getSharedPreferences(context).getString(REGION, "");
    }

    public String getSummonerName(Context context){
        return getSharedPreferences(context).getString(SUMMONER_NAME, "");
    }

    public Boolean isLoggedIn(Context context){
        return getSharedPreferences(context).getBoolean(IS_LOGGED_IN, false);
    }

    public void setRegion(Context context, String region) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(REGION, region);
        editor.apply();
    }

    public void setSummonerName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SUMMONER_NAME, name);
        editor.apply();
    }

    public void setUserStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGGED_IN, bool);
        editor.apply();
    }

    public void clear(Context context){
        setRegion(context, "");
        setSummonerName(context, "");
        setUserStatus(context, false);
    }
}
