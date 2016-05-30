package com.example.tberroa.portal.screens.stats.recent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Map;

class RecentPagerAdapter extends FragmentStatePagerAdapter {

    private final int numberOfTabs;
    private final Bundle incomePlotData;
    private final Bundle offensePlotData;
    private final Bundle utilityPlotData;
    private final Bundle visionPlotData;

    public RecentPagerAdapter(FragmentManager fM, int numberOfTabs, Map<String, Bundle> plotData) {
        super(fM);
        this.numberOfTabs = numberOfTabs;
        incomePlotData = plotData.get("income");
        offensePlotData = plotData.get("offense");
        utilityPlotData = plotData.get("utility");
        visionPlotData = plotData.get("vision");
    }

    @Override
    public Fragment getItem(int position) {
        RecentFragment recentFragment = new RecentFragment();
        switch (position) {
            case 0:
                recentFragment.setArguments(incomePlotData);
                break;
            case 1:
                recentFragment.setArguments(offensePlotData);
                break;
            case 2:
                recentFragment.setArguments(utilityPlotData);
                break;
            case 3:
                recentFragment.setArguments(visionPlotData);
                break;
        }
        return recentFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
