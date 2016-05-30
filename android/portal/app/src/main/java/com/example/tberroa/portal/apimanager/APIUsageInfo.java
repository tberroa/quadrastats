package com.example.tberroa.portal.apimanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.tberroa.portal.data.Params;

public class APIUsageInfo extends Application {

    private final String USAGE = "usage";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("api_key_usage", MODE_PRIVATE);
    }

    public int getUsage(Context context) {
        return getSharedPreferences(context).getInt(USAGE, 0);
    }

    public void increment(Context context) {
        int i = getUsage(context);
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(USAGE, i + 1);
        editor.apply();
        Log.d(Params.TAG_DEBUG, "@APIUsageInfo: usage incremented, currently at " + Integer.toString(i + 1));
    }

    public void reset(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(USAGE, 0);
        editor.apply();
        Log.d(Params.TAG_DEBUG, "@APIUsageInfo: usage reset");
    }
}