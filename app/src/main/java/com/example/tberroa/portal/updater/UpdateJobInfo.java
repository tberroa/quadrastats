package com.example.tberroa.portal.updater;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UpdateJobInfo extends Application{

    private final String STATE = "state";
    private final String IS_RUNNING = "is_running";
    private final String PLAYER_PROFILES = "player_profiles";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("update_job_flags", MODE_PRIVATE);
    }

    public boolean isRunning(Context context){
        return getSharedPreferences(context).getBoolean(IS_RUNNING, false);
    }

    public int getState(Context context){
        return getSharedPreferences(context).getInt(STATE, 0);
    }

    public Map<String, PlayerUpdateProfile> getProfiles(Context context){
        String profilesMapJson = getSharedPreferences(context).getString(PLAYER_PROFILES, "");
        if (!profilesMapJson.equals("")){
            Type typeMap = new TypeToken<Map<String, PlayerUpdateProfile>>(){}.getType();
            return new Gson().fromJson(profilesMapJson, typeMap);
        }
        else{
            return new HashMap<>();
        }
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

    public void setProfiles(Context context, Map<String, PlayerUpdateProfile> profilesMap) {
        Type typeMap = new TypeToken<Map<String, PlayerUpdateProfile>>(){}.getType();
        String profilesMapJson = new Gson().toJson(profilesMap, typeMap);
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PLAYER_PROFILES, profilesMapJson);
        editor.apply();
    }
}
