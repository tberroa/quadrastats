package com.example.tberroa.portal.models.stats;

// This object contains player stats summary information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "PlayerStatsSummaryDto")
public class PlayerStatsSummaryDto extends Model {

    // parent
    @Column(name = "player_stats_summary_list")
    public PlayerStatsSummaryListDto playerStatsSummaryListDto;
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
    @Expose
    @Column(name = "aggregated_stats")
    private AggregatedStatsDto aggregatedStatsDto;

    public PlayerStatsSummaryDto() {
        super();
    }

    public AggregatedStatsDto getAggregatedStatsDto() {
        return new Select().from(AggregatedStatsDto.class)
                .where("player_stats_summary = ?", getId())
                .executeSingle();
    }

    public void cascadeSave() {
        save();
        if (aggregatedStatsDto != null) {
            aggregatedStatsDto.playerStatsSummaryDto = this;
            aggregatedStatsDto.save();
        }
    }
}
