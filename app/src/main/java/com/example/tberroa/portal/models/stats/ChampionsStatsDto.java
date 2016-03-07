package com.example.tberroa.portal.models.stats;

// This object contains a collection of champion stats information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "ChampionsStatsDto")
public class ChampionsStatsDto extends Model {

    // parent
    @Expose
    @Column(name = "ranked_stats")
    RankedStatsDto rankedStatsDto;

    @Expose
    @Column(name = "champion_id")
    public int id;

    @Expose
    @Column(name = "aggregated_stats")
    public AggregatedStatsDto aggregatedStatsDto;

    public ChampionsStatsDto(){
        super();
    }

    public void cascadeSave(){
        save();
        if (aggregatedStatsDto != null){
            aggregatedStatsDto.championsStatsDto = this;
            aggregatedStatsDto.save();
        }
    }
}
