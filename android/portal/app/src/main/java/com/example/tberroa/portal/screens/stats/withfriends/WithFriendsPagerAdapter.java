package com.example.tberroa.portal.screens.stats.withfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tberroa.portal.models.stats.MatchStats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

class WithFriendsPagerAdapter extends FragmentStatePagerAdapter {

    private final int numberOfTabs;
    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;

    public WithFriendsPagerAdapter(FragmentManager fM, int numOfTabs,
                                   Map<Long, Map<String, MatchStats>> matchStatsMapMap) {
        super(fM);
        this.numberOfTabs = numOfTabs;
        this.matchStatsMapMap = matchStatsMapMap;
    }

    @Override
    public Fragment getItem(int position) {
        WithFriendsFragment withFriendsFragment = new WithFriendsFragment();

        Map<String, MatchStats> matchStatsMap = new ArrayList<>(matchStatsMapMap.values()).get(position);
        Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>(){}.getType();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String matchStatMapJson = gson.toJson(matchStatsMap, matchStatsMapType);

        Bundle bundle = new Bundle();
        bundle.putString("match_stats_map", matchStatMapJson);

        withFriendsFragment.setArguments(bundle);
        return withFriendsFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
