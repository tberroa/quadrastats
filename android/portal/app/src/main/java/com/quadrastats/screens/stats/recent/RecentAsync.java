package com.quadrastats.screens.stats.recent;

import com.quadrastats.models.stats.MatchStats;

import java.util.List;

public interface RecentAsync {
    void displayData(List<MatchStats> matchStatsList);
}
