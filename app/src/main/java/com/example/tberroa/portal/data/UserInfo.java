package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo extends Application {

    private final String REGION = "region";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("user_info", MODE_PRIVATE);
    }

    public String getRegion(Context context){
        return getSharedPreferences(context).getString(REGION, "");
    }

    public void setRegion(Context context, String region) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(REGION, region);
        editor.apply();
    }

    public void clear(Context context){
        setRegion(context, "");
    }
}
