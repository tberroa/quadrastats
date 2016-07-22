package com.example.tberroa.portal.screens.stats.withfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.StaticRiotData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

class WFPagerAdapter extends FragmentStatePagerAdapter {

    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;
    private final int numberOfTabs;
    private final StaticRiotData staticRiotData;

    public WFPagerAdapter(FragmentManager fM, int numOfTabs,
                          Map<Long, Map<String, MatchStats>> matchStatsMapMap, StaticRiotData staticRiotData) {
        super(fM);
        numberOfTabs = numOfTabs;
        this.matchStatsMapMap = matchStatsMapMap;
        this.staticRiotData = staticRiotData;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        WFFragment WFFragment = new WFFragment();

        // serialize match stats map
        Map<String, MatchStats> matchStatsMap = new ArrayList<>(matchStatsMapMap.values()).get(position);
        Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
        }.getType();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String matchStatMapJson = gson.toJson(matchStatsMap, matchStatsMapType);

        // serialize static riot data
        Type staticRiotDataType = new TypeToken<StaticRiotData>() {
        }.getType();
        String staticRiotDataJson = new Gson().toJson(staticRiotData, staticRiotDataType);

        Bundle bundle = new Bundle();
        bundle.putString("match_stats_map", matchStatMapJson);
        bundle.putString("static_riot_data", staticRiotDataJson);

        WFFragment.setArguments(bundle);
        return WFFragment;
    }
}
