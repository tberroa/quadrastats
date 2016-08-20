package com.example.tberroa.portal.screens.stats.season;

import com.example.tberroa.portal.models.stats.SeasonStats;

import java.util.Map;

public interface SeasonAsync {
    void displayData(Map<String, Map<Long, SeasonStats>> seasonStatsMapMap);
}
