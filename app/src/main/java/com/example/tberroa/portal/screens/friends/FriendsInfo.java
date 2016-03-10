package com.example.tberroa.portal.screens.friends;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FriendsInfo extends Application {

    private final String NAMES = "names";

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("friends", MODE_PRIVATE);
    }

    public Set<String> getNames(Context context){
        Set<String> defaultValue = new HashSet<>();
        return getSharedPreferences(context).getStringSet(NAMES, defaultValue);
    }

    public void addFriend(Context context, String summonerName) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Set<String> friends = getNames(context);
        friends.add(summonerName);
        editor.putStringSet(NAMES, friends);
        editor.apply();
    }

    public void clear(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Set<String> defaultValue = new HashSet<>();
        editor.putStringSet(NAMES, defaultValue);
        editor.apply();
    }
}
