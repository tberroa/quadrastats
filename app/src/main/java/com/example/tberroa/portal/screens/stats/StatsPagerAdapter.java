package com.example.tberroa.portal.screens.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Map;

class StatsPagerAdapter extends FragmentStatePagerAdapter {

    private final int numberOfTabs;
    private final Bundle goldPlotData;
    private final Bundle offensePlotData;
    private final Bundle utilityPlotData;
    private final Bundle visionPlotData;

    public StatsPagerAdapter(FragmentManager fM, int numberOfTabs, Map<String, Bundle> plotData) {
        super(fM);
        this.numberOfTabs = numberOfTabs;
        goldPlotData = plotData.get("gold");
        offensePlotData = plotData.get("offense");
        utilityPlotData = plotData.get("utility");
        visionPlotData = plotData.get("vision");
    }

    @Override
    public Fragment getItem(int position) {
        StatsFragment statsFragment = new StatsFragment();
        switch (position) {
            case 0:
                statsFragment.setArguments(goldPlotData);
                break;
            case 1:
                statsFragment.setArguments(offensePlotData);
                break;
            case 2:
                statsFragment.setArguments(utilityPlotData);
                break;
            case 3:
                statsFragment.setArguments(visionPlotData);
                break;
        }
        return statsFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
