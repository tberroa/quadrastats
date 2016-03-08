package com.example.tberroa.portal.models.stats;

// This object contains a collection of champion stats information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "ChampionsStatsDto")
public class ChampionsStatsDto extends Model {

    // parent
    @Column(name = "ranked_stats")
    public RankedStatsDto rankedStatsDto;

    @Expose
    @Column(name = "champion_id")
    public int id;

    @Expose
    @Column(name = "aggregated_stats")
    private AggregatedStatsDto aggregatedStatsDto;

    public ChampionsStatsDto(){
        super();
    }

    public AggregatedStatsDto getAggregatedStatsDto(){
        return new Select()
                .from(AggregatedStatsDto.class)
                .where("champion_stats = ?", getId())
                .executeSingle();
    }

    public void cascadeSave(){
        save();
        if (aggregatedStatsDto != null){
            aggregatedStatsDto.championsStatsDto = this;
            aggregatedStatsDto.save();
        }
    }
}
