package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfo extends Application {

    private final String EMAIL = "email";
    private final String ID = "id";
    private final String IS_SIGNED_IN = "is_signed_in";

    public void clear(Context context) {
        setId(context, 0);
        setSignInStatus(context, false);
    }

    public String getEmail(Context context) {
        return sharedPreferences(context).getString(EMAIL, "");
    }

    public long getId(Context context) {
        return sharedPreferences(context).getLong(ID, 0);
    }

    public Boolean getSignInStatus(Context context) {
        return sharedPreferences(context).getBoolean(IS_SIGNED_IN, false);
    }

    public void setEmail(Context context, String email) {
        Editor editor = sharedPreferences(context).edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public void setId(Context context, long id) {
        Editor editor = sharedPreferences(context).edit();
        editor.putLong(ID, id);
        editor.apply();
    }

    public void setSignInStatus(Context context, Boolean bool) {
        Editor editor = sharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, bool);
        editor.apply();
    }

    private SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }
}
