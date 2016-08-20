package com.quadrastats.screens.stats.season;

import com.quadrastats.models.stats.SeasonStats;

import java.util.Map;

public interface SeasonAsync {
    void displayData(Map<String, Map<Long, SeasonStats>> seasonStatsMapMap);
}
