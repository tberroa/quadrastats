package com.quadrastats.screens.stats.withfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.stats.MatchStats;
import com.quadrastats.screens.StaticRiotData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

class PageAdapter extends FragmentStatePagerAdapter {

    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;
    private final int numberOfTabs;
    private final StaticRiotData staticRiotData;

    public PageAdapter(FragmentManager fM, int numOfTabs,
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
        WFFragment wfFragment = new WFFragment();

        // serialize match stats map
        Map<String, MatchStats> matchStatsMap = new ArrayList<>(matchStatsMapMap.values()).get(position);
        Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
        }.getType();
        String matchStatMapJson = ModelUtil.toJsonStringMap(matchStatsMap, matchStatsMapType);

        // serialize static riot data
        Type staticRiotDataType = new TypeToken<StaticRiotData>() {
        }.getType();
        String staticRiotDataJson = new Gson().toJson(staticRiotData, staticRiotDataType);

        Bundle bundle = new Bundle();
        bundle.putString("match_stats_map", matchStatMapJson);
        bundle.putString("static_riot_data", staticRiotDataJson);

        wfFragment.setArguments(bundle);
        return wfFragment;
    }
}
