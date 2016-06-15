package com.example.tberroa.portal.screens.stats.recent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RecentPagerAdapter extends FragmentStatePagerAdapter {

    private final int numberOfTabs;
    private final Bundle incomeBundle = new Bundle();
    private final Bundle offenseBundle = new Bundle();
    private final Bundle utilityBundle = new Bundle();
    private final Bundle visionBundle = new Bundle();

    public RecentPagerAdapter(FragmentManager fM, int numOfTabs,
                              ArrayList<String> titles, Map<String, List<List<Number>>> data) {
        super(fM);
        this.numberOfTabs = numOfTabs;

        // split the data by tab
        Map<String, List<List<Number>>> incomeData = new HashMap<>();
        Map<String, List<List<Number>>> offenseData = new HashMap<>();
        Map<String, List<List<Number>>> utilityData = new HashMap<>();
        Map<String, List<List<Number>>> visionData = new HashMap<>();

        if (!data.isEmpty()) {
            for (Map.Entry<String, List<List<Number>>> entry : data.entrySet()) {
                List<List<Number>> summonerData = entry.getValue();
                incomeData.put(entry.getKey(), summonerData.subList(0, 4));
                offenseData.put(entry.getKey(), summonerData.subList(4, 6));
                utilityData.put(entry.getKey(), summonerData.subList(6, 9));
                visionData.put(entry.getKey(), summonerData.subList(9, 12));
            }
        }

        // serialize the data
        Gson gson = new Gson();
        Type plotType = new TypeToken<Map<String, List<List<Number>>>>() {
        }.getType();
        String incomeDataJson = gson.toJson(incomeData, plotType);
        String offenseDataJson = gson.toJson(offenseData, plotType);
        String utilityDataJson = gson.toJson(utilityData, plotType);
        String visionDataJson = gson.toJson(visionData, plotType);

        // create bundles
        incomeBundle.putStringArrayList("plot_titles", new ArrayList<>(titles.subList(0, 4)));
        incomeBundle.putString("plot_data", incomeDataJson);
        offenseBundle.putStringArrayList("plot_titles", new ArrayList<>(titles.subList(4, 6)));
        offenseBundle.putString("plot_data", offenseDataJson);
        utilityBundle.putStringArrayList("plot_titles", new ArrayList<>(titles.subList(6, 9)));
        utilityBundle.putString("plot_data", utilityDataJson);
        visionBundle.putStringArrayList("plot_titles", new ArrayList<>(titles.subList(9, 12)));
        visionBundle.putString("plot_data", visionDataJson);
    }

    @Override
    public Fragment getItem(int position) {
        RecentFragment recentFragment = new RecentFragment();
        switch (position) {
            case 0:
                recentFragment.setArguments(incomeBundle);
                break;
            case 1:
                recentFragment.setArguments(offenseBundle);
                break;
            case 2:
                recentFragment.setArguments(utilityBundle);
                break;
            case 3:
                recentFragment.setArguments(visionBundle);
                break;
        }
        return recentFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
