package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String REGION = "region";
    private final String KEY = "key";
    private final String NAME = "name";
    private final String ID = "id";
    private final String ICON = "icon";
    private final String IS_SIGNED_IN = "is_signed_in";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public String getRegion(Context context) {
        return getSharedPreferences(context).getString(REGION, "");
    }

    public String getKey(Context context) {
        return getSharedPreferences(context).getString(KEY, "");
    }

    public String getName(Context context) {
        return getSharedPreferences(context).getString(NAME, "");
    }

    public long getId(Context context) {
        return getSharedPreferences(context).getLong(ID, 0);
    }

    public int getIcon(Context context) {
        return getSharedPreferences(context).getInt(ICON, 0);
    }

    public Boolean isSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
    }

    public void setRegion(Context context, String region) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(REGION, region);
        editor.apply();
    }

    public void setKey(Context context, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY, key);
        editor.apply();
    }

    public void setName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void setId(Context context, long id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(ID, id);
        editor.apply();
    }

    public void setIcon(Context context, int icon) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(ICON, icon);
        editor.apply();
    }

    public void setSignInStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    public void clear(Context context) {
        setRegion(context, "");
        setKey(context, "");
        setName(context, "");
        setId(context, 0);
        setIcon(context, 0);
        setSignInStatus(context, false);
    }
}
