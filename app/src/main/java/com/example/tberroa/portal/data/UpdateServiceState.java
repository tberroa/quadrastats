package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UpdateServiceState extends Application{

    private final String STATE = "code";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("most_recent_error", MODE_PRIVATE);
    }

    public int get(Context context){
        return getSharedPreferences(context).getInt(STATE, 0);
    }

    public void set(Context context, int state) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(STATE, state);
        editor.apply();
    }
}
