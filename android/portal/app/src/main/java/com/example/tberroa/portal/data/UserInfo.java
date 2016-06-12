package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String IS_SIGNED_IN = "is_signed_in";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public Boolean isSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
    }

    public void setSignInStatus(Context context, Boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    public void clear(Context context) {
        setSignInStatus(context, false);
    }
}
