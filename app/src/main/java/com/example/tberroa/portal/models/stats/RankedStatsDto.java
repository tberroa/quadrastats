package com.example.tberroa.portal.models.stats;

// This object contains ranked stats information.

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "RankedStatsDto")
public class RankedStatsDto extends Model {

    @Expose
    @Column(name = "champions")
    private List<ChampionsStatsDto> champions;

    @Expose
    @Column(name = "modify_date")
    public long modifyDate;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    public RankedStatsDto(){
        super();
    }

    public List<ChampionsStatsDto> getChampions(){
        return getMany(ChampionsStatsDto.class, "ranked_stats");
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (champions != null) {
                for (ChampionsStatsDto champion : champions) {
                    champion.rankedStatsDto = this;
                    champion.cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
