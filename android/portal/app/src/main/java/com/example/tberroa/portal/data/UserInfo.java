package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String NAME = "name";
    private final String ID = "id";
    private final String IS_SIGNED_IN = "is_signed_in";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public String getName(Context context) {
        return getSharedPreferences(context).getString(NAME, "");
    }

    public long getId(Context context) {
        return getSharedPreferences(context).getLong(ID, 0);
    }

    public Boolean isSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
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

    public void setSignInStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    public void clear(Context context) {
        setName(context, "");
        setId(context, 0);
        setSignInStatus(context, false);
    }
}
