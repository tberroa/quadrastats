package com.example.tberroa.portal.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

class ErrorInfo extends Application {

    private final String CODE = "code";

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("most_recent_error", MODE_PRIVATE);
    }

    public String getCode(Context context) {
        return getSharedPreferences(context).getString(CODE, "");
    }

    public void setCode(Context context, String code) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CODE, code);
        editor.apply();
    }
}
