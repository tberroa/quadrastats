package com.example.tberroa.portal.models.stats;

// This object contains player stats summary information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "PlayerStatsSummaryDto")
public class PlayerStatsSummaryDto extends Model {

    public PlayerStatsSummaryDto(){
    }

    @Expose
    @Column(name = "aggregated_stats")              // 	Aggregated stats.
    public AggregatedStatsDto aggregatedStatsDto;

    @Expose
    @Column(name = "losses")                        // Number of losses for this queue type.
    public int losses;                              // Returned for ranked queue types only.


    @Expose
    @Column(name = "modify_date")                   // Date stats were last modified specified as epoch milliseconds.
    public long modifyDate;

    @Expose
    @Column(name = "player_stat_summary_type")      // Player stats summary type. (Legal values: AramUnranked5x5,
    public String playerStatSummaryType;            // Ascension, Bilgewater, CAP5x5, CoopVsAI, CoopVsAI3x3, CounterPick,
                                                    // FirstBlood1x1, FirstBlood2x2, Hexakill, KingPoro, NightmareBot,
                                                    // OdinUnranked, OneForAll5x5, RankedPremade3x3, RankedPremade5x5,
                                                    // RankedSolo5x5, RankedTeam3x3, RankedTeam5x5, SummonersRift6x6,
                                                    // Unranked, Unranked3x3, URF, URFBots)

    @Expose
    @Column(name = "wins")                          // Number of wins for this queue type.
    public int wins;

}
