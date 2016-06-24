package com.example.tberroa.portal.screens.stats.recent;

import com.example.tberroa.portal.models.stats.MatchStats;

import java.util.List;

public interface RecentAsync {
    void displayData(List<MatchStats> matchStatsList);
}
