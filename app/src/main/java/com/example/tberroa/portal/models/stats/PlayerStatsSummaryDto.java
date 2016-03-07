package com.example.tberroa.portal.models.stats;

// This object contains player stats summary information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "PlayerStatsSummaryDto")
public class PlayerStatsSummaryDto extends Model {

    // parent
    @Expose
    @Column(name = "player_stats_summary_list")
    PlayerStatsSummaryListDto playerStatsSummaryListDto;

    @Expose
    @Column(name = "aggregated_stats")
    public AggregatedStatsDto aggregatedStatsDto;

    @Expose
    @Column(name = "losses")
    public int losses;

    @Expose
    @Column(name = "modify_date")
    public long modifyDate;

    @Expose
    @Column(name = "player_stat_summary_type")
    public String playerStatSummaryType;

    @Expose
    @Column(name = "wins")
    public int wins;

    public PlayerStatsSummaryDto(){
        super();
    }

    public void cascadeSave(){
        save();
        if (aggregatedStatsDto != null){
            aggregatedStatsDto.playerStatsSummaryDto = this;
            aggregatedStatsDto.save();
        }
    }
}
