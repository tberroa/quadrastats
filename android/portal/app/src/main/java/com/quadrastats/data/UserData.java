package com.quadrastats.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserData {

    private static final String EMAIL = "email";
    private static final String ID = "id";
    private static final String IS_SIGNED_IN = "is_signed_in";

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

    public boolean isSignedIn(Context context) {
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

    public void setSignInStatus(Context context, Boolean status) {
        Editor editor = sharedPreferences(context).edit();
        editor.putBoolean(IS_SIGNED_IN, status);
        editor.apply();
    }

    private SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }
}
