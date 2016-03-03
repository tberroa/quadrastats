package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SummonerInfo extends Application {

    private final String REGION = "region";
    private final String BASIC_NAME = "basic_name";
    private final String STYLIZED_NAME = "stylized_name";
    private final String IS_SIGNED_IN = "is_logged_in";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("summoner_info", MODE_PRIVATE);
    }

    public String getRegion(Context context){
        return getSharedPreferences(context).getString(REGION, "");
    }

    public String getBasicName(Context context){
        return getSharedPreferences(context).getString(BASIC_NAME, "");
    }

    public String getStylizedName(Context context) {
        return getSharedPreferences(context).getString(STYLIZED_NAME, "");
    }

    public Boolean isSignedIn(Context context){
        return getSharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
    }

    public void setRegion(Context context, String region) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(REGION, region);
        editor.apply();
    }

    public void setBasicName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASIC_NAME, name);
        editor.apply();
    }

    public void setStylizedName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(STYLIZED_NAME, name);
        editor.apply();
    }

    public void setSummonerStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    public void clear(Context context){
        setRegion(context, "");
        setBasicName(context, "");
        setStylizedName(context, "");
        setSummonerStatus(context, false);
    }
}
