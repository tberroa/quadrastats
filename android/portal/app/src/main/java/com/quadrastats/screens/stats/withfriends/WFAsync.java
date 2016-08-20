package com.quadrastats.screens.stats.withfriends;

import com.quadrastats.models.stats.MatchStats;

import java.util.Map;

public interface WFAsync {
    void displayData(Map<Long, Map<String, MatchStats>> matchStatsMapMap);
}