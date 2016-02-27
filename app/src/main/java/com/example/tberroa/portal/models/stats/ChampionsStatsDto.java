package com.example.tberroa.portal.models.stats;

// This object contains a collection of champion stats information.

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ChampionsStatsDto")
public class ChampionsStatsDto {

    @Expose
    @Column(name = "id")                        // Champion ID.  Note that champion ID 0 represents
    public int id;                              // the combined stats for all champions

    @Expose
    @Column(name = "aggregated_stats_dto")      // Aggregated stats associated with the champion.
    public AggregatedStatsDto aggregatedStatsDto;

}
