package com.example.tberroa.portal.updater;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UpdateJobInfo extends Application{

    private final String STATE = "state";
    private final String IS_RUNNING = "is_running";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("update_job_flags", MODE_PRIVATE);
    }

    public boolean isRunning(Context context){
        return getSharedPreferences(context).getBoolean(IS_RUNNING, false);
    }

    public int getState(Context context){
        return getSharedPreferences(context).getInt(STATE, 0);
    }

    public void setRunning(Context context, boolean bool){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_RUNNING, bool);
        editor.apply();
    }

    public void setState(Context context, int state) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(STATE, state);
        editor.apply();
    }
}
