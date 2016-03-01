package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MostRecentError extends Application {

    private final String CODE = "code";
    private final String MESSAGE = "message";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("most_recent_error", MODE_PRIVATE);
    }

    public int getCode(Context context){
        return getSharedPreferences(context).getInt(CODE, 0);
    }

    public String getMessage(Context context){
        return getSharedPreferences(context).getString(MESSAGE, "");
    }

    public void setCode(Context context, int code) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(CODE, code);
        editor.apply();
    }

    public void setMessage(Context context, String message) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(MESSAGE, message);
        editor.apply();
    }

}
