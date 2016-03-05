package com.example.tberroa.portal.models.stats;

// This object contains a collection of player stats summary information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "PlayerStatsSummaryListDto")
public class PlayerStatsSummaryListDto extends Model {

    @Expose
    @Column(name = "player_stat_summaries")
    public List<PlayerStatsSummaryDto> playerStatSummaries; // Collection of player stats summaries associated with the summoner.

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    public PlayerStatsSummaryListDto(){
        super();
    }
}
