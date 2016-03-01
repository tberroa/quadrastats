package com.example.tberroa.portal.models.stats;

// This object contains ranked stats information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "RankedStatsDto")
public class RankedStatsDto extends Model {

    public RankedStatsDto(){
    }

    @Expose
    @Column(name = "champions")
    public List<ChampionsStatsDto> champions;   // Collection of aggregated stats summarized by champion.

    @Expose
    @Column(name = "modify_date")               // Date stats were last modified specified as epoch milliseconds.
    public long modifyDate;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;                     // SummonerDto ID.
}
