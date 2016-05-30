package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String IS_SIGNED_IN = "is_signed_in";
    private final String REGION = "region";
    private final String ID = "id";
    private final String BASIC_NAME = "basic_name";
    private final String STYLIZED_NAME = "stylized_name";
    private final String ICON_ID = "user_icon_id";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public String getRegion(Context context) {
        return getSharedPreferences(context).getString(REGION, "");
    }

    public long getId(Context context) {
        return getSharedPreferences(context).getLong(ID, 0);
    }

    public int getIconId(Context context) {
        return getSharedPreferences(context).getInt(ICON_ID, 0);
    }

    public String getBasicName(Context context) {
        return getSharedPreferences(context).getString(BASIC_NAME, "");
    }

    public String getStylizedName(Context context) {
        return getSharedPreferences(context).getString(STYLIZED_NAME, "");
    }

    public Boolean isSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
    }

    public void setRegion(Context context, String region) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(REGION, region);
        editor.apply();
    }

    public void setId(Context context, long id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(ID, id);
        editor.apply();
    }

    public void setIconId(Context context, int id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(ICON_ID, id);
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

    public void setStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    public void clear(Context context) {
        setRegion(context, "");
        setBasicName(context, "");
        setStylizedName(context, "");
        setId(context, 0);
        setIconId(context, 0);
        setStatus(context, false);
    }
}
