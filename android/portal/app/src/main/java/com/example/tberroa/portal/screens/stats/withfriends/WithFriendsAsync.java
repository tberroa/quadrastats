package com.example.tberroa.portal.screens.stats.withfriends;

import com.example.tberroa.portal.models.stats.MatchStats;

import java.util.Map;

public interface WithFriendsAsync {
    void displayData(Map<Long, Map<String, MatchStats>> matchStatsMapMap);
}